package com.mydigipay.los.ruleautomation.service.stepservices.register;

import com.mydigipay.los.ruleautomation.exception.ImportException;
import com.mydigipay.los.ruleautomation.exception.RegisterException;
import com.mydigipay.los.ruleautomation.model.rule.Rule;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.service.converter.CustomDateConverter;
import com.mydigipay.los.ruleautomation.service.dbservices.MongoExecuteQuery;
import com.mydigipay.los.ruleautomation.service.excelInputProvider.CreateExcelData;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
/*
 * Author: f.bahramnejad
 */
public class RegisterService {
    final
    ConfigUtil configUtil;
    //    @Value("src/main/resources/ds/OrganizationalTemplate.xlsx")
    String orgFileLocation;
    //    @Value("src/main/resources/ds/individualTemplate.xlsx")
    String indFileLocation;
    @Autowired
    private MongoExecuteQuery execQuery;

    public RegisterService(ConfigUtil configUtil) {
        this.configUtil = configUtil;
        orgFileLocation= configUtil.getProperty("los.org-excel-template");
        indFileLocation= configUtil.getProperty("los.individual-excel-template");
    }

    private RestUtil getRestUtil() {
        return new RestUtil(configUtil.getProperty("staging.credit-base-url-ms"));
    }

    private String waitForImportComplete(String trackingCode) throws ImportException, InterruptedException {
        String uploadStatus;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("User-Id", HttpHeader.adminUserId);
        RestUtil trackUpload = getRestUtil().headers(headers);
        String body = "{\n" +
                "    \"restrictions\":[{\n" +
                "    \"type\":\"simple\",\n" +
                "    \"field\":\"trackingCode\",\n" +
                "    \"value\":\"" + trackingCode + "\",\n" +
                "    \"operation\":\"eq\"}]}";

        trackUpload = trackUpload.path("/users/admin/upload/search").body(body).post();
        Response trackResp = trackUpload.getResponse();
        uploadStatus = trackResp.body().jsonPath().getString("detailsList[0].status");
        switch (uploadStatus) {
            case "INITIATED":

                Thread.sleep(15000);
                waitForImportComplete(trackingCode);
            case "SUCCESS":

                return trackResp.body().jsonPath().getString("detailsList[0].creditId");
            case "FAILED":
                throw new ImportException("ImportException" + trackResp.body().jsonPath().getList(
                        "detailsList[0].errorMessages").toString());

        }

        return null;

    }


    public String orgRegisterByImport(String groupId, Rule rule, User user, String crn) throws IOException, ImportException, InterruptedException {
        this.changeUploadedFileName("OrganizationalTemplate.xlsx");
        CreateExcelData.createOrganizationalImportFile(orgFileLocation, rule, user, crn);
        String creditId = registerByImport(orgFileLocation, groupId);
        this.changeUploadedFileName("OrganizationalTemplate.xlsx");
        return creditId;

    }

    public String individualRegisterByImport(String groupId, Rule rule, User user) throws IOException, ImportException, InterruptedException {
        this.changeUploadedFileName("individualTemplate.xlsx");
        CreateExcelData.createIndividualImportFile(indFileLocation, rule, user);
        String creditId = registerByImport(indFileLocation, groupId);
        this.changeUploadedFileName("individualTemplate.xlsx");
        return creditId;
    }

    private String registerByImport(String filePath, String groupId) throws ImportException, InterruptedException {
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("User-Id", HttpHeader.adminUserId);
        customHeaders.put("Group-Id", groupId);
        customHeaders.put("Content-Type", HttpHeader.formData);

        RestUtil request = getRestUtil().headers(customHeaders);
        request = request.path("/users/admin/upload")
                .multiPart("file", filePath,
                        "application/vnd.ms-excel")
                .post();
        Response resp = request.getResponse();
        String trackingCode = resp.body().jsonPath().getString("trackingCode");
        if (trackingCode != null) {
            return waitForImportComplete(trackingCode);

        } else {
            throw new ImportException("ImportException" + resp.body().jsonPath().getString("result.message"));

        }
    }

    private String findPlanId(String groupId) {
        execQuery.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        execQuery.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.planCol"));
        //MongoExecuteQuery execQuery = new MongoExecuteQuery(configUtil.getProperty("databases.switch-credit.name"),
        //     configUtil.getProperty("databases.switch-credit.collections.planCol"));
        // ("switch_credit_db", "plans");
        JSONObject body = new JSONObject();
        body.put("groupDetail.groupId", groupId);
        Response findPlan = execQuery.find(body.toJSONString());

        return findPlan.body().jsonPath().getList("planId").get(0).toString();

    }

    public String registerByPlan(String groupId, User user) throws RegisterException, ParseException {
        String planId = findPlanId(groupId);
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        RestUtil request = getRestUtil().headers(headers);
        Map<String, Object> body = new HashMap<>();
        body.put("nationalCode", user.getNationalCode());
        body.put("birthDate", CustomDateConverter.jalaliToTimeStamp(user.getBirthDate()));
        body.put("groupId", groupId);
        body.put("planId", planId);
        request = request.path("/volunteers/pre-register").body(body).post();
        Response resp = request.getResponse();
        String creditId = resp.body().jsonPath().getString("creditId");
        if (creditId == null) {
            throw new RegisterException(resp.body().jsonPath().getString("message"));
        } else {
            return creditId;
        }
    }


    private void changeUploadedFileName(String fileName) {
        Instant instant = Instant.now();
        execQuery.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        execQuery.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.userBulkCol"));
        String query = "{\n" +
                "\t\"findQuery\": {\n" +
                "\t\"name\": \"" + fileName + "\"\n" +
                "\t},\n" +
                "\t\"updateQuery\": {\n" +
                "\t\"$set\": {\n" +
                "\t\"name\": \"" + "usedByRuleAutomation" + instant.toEpochMilli() + ".xlsx" + "\"\n" +
                "\t}\n" +
                "\t}\n" +
                "}";
        execQuery.updateOne(query);
    }

}
