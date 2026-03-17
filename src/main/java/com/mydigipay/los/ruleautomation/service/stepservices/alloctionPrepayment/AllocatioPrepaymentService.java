package com.mydigipay.los.ruleautomation.service.stepservices.alloctionPrepayment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.profile.OnboardingProfile;
import com.mydigipay.los.ruleautomation.model.profile.pojo.fee.SettleType;
import com.mydigipay.los.ruleautomation.model.rule.Rule;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.repository.CreditProfileRepository;
import com.mydigipay.los.ruleautomation.repository.RuleRepository;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.service.converter.CustomDateConverter;
import com.mydigipay.los.ruleautomation.service.dashboardservices.DashboardLoginService;
import com.mydigipay.los.ruleautomation.service.dbservices.MongoExecuteQuery;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AllocatioPrepaymentService {
    final DashboardLoginService dashboardLoginService;
    ConfigUtil configUtil;
    CreditProfileRepository profileRepo;
    RuleRepository ruleRepo;
    MongoExecuteQuery mongoService;
    FetchActivation fetch;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> dashboardHeaders = new HashMap<>();
    private RestUtil request;
    private RestUtil dashboardRequest;


    public AllocatioPrepaymentService(ConfigUtil configUtil, FetchActivation fetch, DashboardLoginService dashboardLoginService, RuleRepository ruleRepo, CreditProfileRepository profileRepo, MongoExecuteQuery execQuery) {

        this.configUtil = configUtil;
        this.fetch = fetch;
        this.dashboardLoginService = dashboardLoginService;
        this.ruleRepo = ruleRepo;
        this.profileRepo=profileRepo;
        this.mongoService = execQuery;
    }

    private void setupRequest(User user) {
        request = RestUtil.init(configUtil.getProperty("staging.credit-base-url-ms"));
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        headers.put("agent", HttpHeader.agent);

        String adminToken = dashboardLoginService.getToken();
        dashboardHeaders.put("Authorization", "Bearer " + adminToken);
        dashboardHeaders.put("Content-Type", HttpHeader.contentType);
    }
    //private void completeAllOtherSteps
    public boolean checkAllocationPrepaymentAmount(User user, Activation activation) throws Exception {
        setupRequest(user);
        Map<String, Object> body = new HashMap<>();
        body.put("creditId", activation.getCreditId());
        body.put("fundProviderCode", activation.getGroup().getFundProvider().get("fpCode"));
        JsonPath response = request.headers(headers).path("allocation-prepayments/configs").body(body).post().getApiResponseAsJson();
        double amount = response.getLong("amount");
        Rule rule = ruleRepo.findByRuleId(activation.getGroup().getRuleId());
        OnboardingProfile profile= profileRepo.findByProfileId(rule.getProfileId());
        Double feechargePercentage = profile.getPaymentTerm().getFeechargePercentage(SettleType.ON_ALLOCATION);
        Double prepaymentPercentage = profile.getPaymentTerm().getFpFeechargePercentage(SettleType.ON_ALLOCATION);
        long balance = activation.getInitialBalance();
        long expectedAmount = Math.round((feechargePercentage + prepaymentPercentage) / 100 * balance);
        if (amount == expectedAmount) {
            return true;
        } else {
            throw new Exception("Wrong allocation prepayment amount: " + amount + "expected amount is: " + expectedAmount);
        }
    }

    private boolean waitForStartProcess(Activation activation) {
        mongoService.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.allocationCol"));
        String body = "{\n" +
                "    \"creditId\":\"" + activation.getCreditId() + "\"\n" +
                "}";
        Response response = mongoService.find(body);
        return response.print().equals("[]");
    }

    public void changeAllocationStatus(Activation activation) throws InterruptedException {
        while (waitForStartProcess(activation)) {
            Thread.sleep(30000);
        }
        mongoService.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.allocationCol"));
        String body = "{\n" +
                "\t\"findQuery\": {\n" +
                "\t\t\"creditId\": \"" + activation.getCreditId() + "\"\n" +
                "\t},\n" +
                "\t\"updateQuery\": {\n" +
                "\t\t\"$set\": {\n" +
                "\t\t\t\"status\": \"IN_PROGRESS\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
        mongoService.updateOne(body);

    }

    private void autoAllocationSwitch(Activation activation, Boolean isOn) {
        Map<String, Object> body = new HashMap<>();
        body.put("autoProcess", isOn);
        body.put("fundProviderCode", activation.getGroup().getFundProvider().get("fpCode"));
        dashboardRequest = RestUtil.init(configUtil.getProperty("staging.dashboard-base-url"))
                .headers(dashboardHeaders);
        dashboardRequest.path("credit/admin/allocation-setting/update").body(body).post();
    }

    private List<String> allocationData(User user, Activation activation, String contractNum) {
        List<String> result;
        int fpCode = (int) activation.getGroup().getFundProvider().get("fpCode");
        if (fpCode == 1) {
            result = new ArrayList<>(20);
            result.add("1");
            result.add(user.getNationalCode());
            result.add(user.getName());
            result.add(user.getSureName());
            result.add(String.valueOf(activation.getInitialBalance()));
            result.add("موفق");
            result.add(contractNum);


            for (int i = 8; i < 21; i++) {
                result.add("");
            }
            return result;
        } else if (fpCode == 13) {
            result = new ArrayList<>(13);
            result.add(CustomDateConverter.getTodayInJalali());
            result.add(user.getNationalCode());
            result.add("35220");
            result.add(user.getName());
            result.add(user.getSureName());
            result.add(user.getCellNumber());
            result.add(String.valueOf(activation.getInitialBalance()));
            result.add("3441050649");
            result.add(contractNum);
            result.add(CustomDateConverter.getTodayInJalali());
            result.add(String.valueOf(activation.getInstallmentCount()));
            result.add("success");
            result.add("");
            return result;

        }
        return null;
    }

    public void importAllocationFile(User user, Activation activation, String contractNum) throws Exception {
        autoAllocationSwitch(activation, false);
        List<Object> data = new ArrayList<>();
        data.add(allocationData(user, activation, contractNum));
        Map<String, Object> body = new HashMap<>();
        body.put("data", data);
        Instant instant = Instant.now();
        body.put("fileName", "AutomationTestImport" + instant.toEpochMilli() + ".xlsx");
        body.put("fundProviderCode", activation.getGroup().getFundProvider().get("fpCode"));
        String trackingCode = "AutomationTest" + instant.toEpochMilli();
        body.put("trackingCode", trackingCode);
        body.put("contractStartDate", instant.toEpochMilli());
        dashboardRequest.path("credit/admin/allocation-prepayments/import").body(body).post();
        waitForAllocationComplete(trackingCode);

    }

    private void waitForAllocationComplete(String trackinCode) throws Exception {
        mongoService.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.allocationJournalCol"));
        String body = "{\n" +
                "\"trackingCode\":\"" + trackinCode + "\"\n" +
                "}";

        Response resp = mongoService.find(body);
        String status = resp.jsonPath().getString("status");
        if (status.equals("[INITIALIZED]")) {
            Thread.sleep(5000);
            waitForAllocationComplete(trackinCode);
        } else if (!status.equals("[SUCCESS]")) {
            String msg = resp.jsonPath().getString("message");
            if (msg.equals("[]")) {
                throw new Exception("Allocation prepayment status is: " + status + " with no reason");
            } else {
                throw new Exception(resp.jsonPath().getString("message"));
            }

        }

    }

    public void automaticAllocation(Activation activation) throws Exception {
        autoAllocationSwitch(activation, true);
        while (waitForStartProcess(activation)) {
            Thread.sleep(30000);
        }
        mongoService.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.allocationCol"));
        Map<String, Object> body = new HashMap<>();
        body.put("creditId", activation.getCreditId());
        Response resp = mongoService.find(body);
        JsonNode jsonNode = new ObjectMapper().readTree(resp.print());
        String trackingCode = jsonNode.get(0).get("autoProcessTrackingCode").toString();
        waitForAllocationComplete(trackingCode);
    }
public void completePreviousSteps(Activation activation){
    mongoService.setDbName(configUtil.getProperty("databases.switch-credit.name"));
    mongoService.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.activationCol"));
    Instant instant = Instant.now();
    String body = "{\n" +
            "\t\"findQuery\": {\n" +
            "\t\t\"creditId\": \""+activation.getCreditId()+"\"},\n" +
            "        \n" +
            "\t\"updateQuery\": {\n" +
            "\t\t\"$set\": {\n" +
            "\t\t\t\"steps.$[].completedDate\": "+instant.toEpochMilli()+"\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}";
    mongoService.updateOne(body);
    }
}
