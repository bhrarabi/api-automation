package com.mydigipay.los.ruleautomation.service.stepservices.checkCreditFile;

import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.service.dbservices.MongoExecuteQuery;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class SamatService {
    final
    ConfigUtil configUtil;
    final
    FetchActivation fetch;
    private final MongoExecuteQuery mongoService;

    public SamatService(ConfigUtil configUtil, FetchActivation fetch, MongoExecuteQuery mongoService) {
        this.configUtil = configUtil;
        this.fetch = fetch;
        this.mongoService = mongoService;

    }
    private RestUtil prepareRestUtilInstance(User user) {
        RestUtil request = new RestUtil(configUtil.getProperty("staging.credit-base-url-ms"));
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        headers.put("agent", HttpHeader.agent);
        return request.headers(headers);
    }
    public void SamatInquiry(Activation activation, User user){
        setUserHistory(user);
        prepareRestUtilInstance(user).pathParam("creditId", activation.getCreditId())
                .path("check-credit-file/inquiry/{creditId}").get();

    }
    private void setSamatLoanHistory(User user){
        mongoService.setDbName(configUtil.getProperty("databases.kyc.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.kyc.collections.loans"));
        Instant instant = Instant.now();
        String body="{\n" +
                "\t\"findQuery\": {\n" +
                "\t\t\"initiator\": \"ruleAutomationTest\"\n" +
                "\t},\n" +
                "\t\"updateQuery\": {\n" +
                "\t\t\"$set\": {\n" +
                "\t\t\t\"nationalCode\": \""+user.getNationalCode()+"\"\n" +
                "            , \n" +
                "    \"creationDate\":"+instant.toEpochMilli()+"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
        mongoService.updateOne(body);
    }
    private void setSamatchequeHistory(User user){
        mongoService.setDbName(configUtil.getProperty("databases.kyc.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.kyc.collections.cheques"));
        Instant instant = Instant.now();
        String body="{\n" +
                "\t\"findQuery\": {\n" +
                "\t\t\"initiator\": \"ruleAutomationTest\"\n" +
                "\t},\n" +
                "\t\"updateQuery\": {\n" +
                "\t\t\"$set\": {\n" +
                "\t\t\t\"nationalCode\": \""+user.getNationalCode()+"\"\n" +
                "            , \n" +
                "    \"creationDate\":"+instant.toEpochMilli()+"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
        mongoService.updateOne(body);
    }
    private void setUserHistory(User user){
        setSamatchequeHistory(user);
        setSamatLoanHistory(user);
    }
}
