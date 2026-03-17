package com.mydigipay.los.ruleautomation.service.dashboardservices;

import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.service.dbservices.MongoExecuteQuery;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Lazy
public class ArchiveActivation {
    @Autowired
    ConfigUtil configUtil;
    @Autowired
    MongoExecuteQuery mongoService;
    @Autowired
    FetchActivation fetch;
    private RestUtil restUtil;

    private void dshRestUtil() {
        restUtil = new RestUtil(configUtil.getProperty("staging.credit-base-url-ms"));
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", HttpHeader.adminUserId);
        headers.put("Content-Type", HttpHeader.contentType);
        restUtil.headers(headers);
    }

    /**
     * This method cancels the given activation. We use this fuction only for onging activations.
     *
     * @param activation Activation that we want to archive it
     */
    private void archiveActivation(Activation activation) {
        dshRestUtil();
        JSONObject body = new JSONObject();
        body.put("creditId", activation.getCreditId());
        restUtil.path("/activations/admin/cancel").body(body).post();
    }

    /**
     * This method closes an active activation.
     *
     * @param activation Activation that we want close it
     */
    private void closeActivation(Activation activation, Boolean isAutoClose) throws InterruptedException {
        mongoService.setDbName(configUtil.getProperty("databases.credit-account-mng.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.credit-account-mng.collections.accounts"));
        String body = "{\n" +
                "\"findQuery\":{\"creditId\":\""+activation.getCreditId()+"\"},\"updateQuery\":{\"$set\":{\"contracts.$[].status\":1, \"billingCycles.$[].status\":1, \"expirationTime\":"+Instant.now().toEpochMilli()+"}}\n" +
                "}";

        mongoService.updateOne(body);
        if(!isAutoClose){
        while (!activation.getStatus().equals("READY_TO_CLOSE")) {
            Thread.sleep(5000);
            activation = fetch.fetchByCreditId(activation.getCreditId());
        }}
        dshRestUtil();
        restUtil.pathParam("creditId", activation.getCreditId()).path("/activations/admin/close/review/{creditId}/21").post().getResponse().print();
    }

    /**
     * This method checks activation status and based on it's status calls one of archive or close methods.
     *
     * @param activation Activation that we want archive or close it
     */
    public void cancelActivation(Activation activation, Boolean isAutoClose) throws InterruptedException {
        String status = activation.getStatus();
        if (status.equals("ACTIVE")) {
            closeActivation(activation,isAutoClose);
        } else if (status.equals("ALLOCATION_PREPAYMENT_READY_TO_APPROVE") || status.equals("ALLOCATION_PREPAYMENT_ACCEPTED")) {
            setStatus(activation, "ALLOCATION_PREPAYMENT_REJECTED");
            archiveActivation(activation);
        } else {
            archiveActivation(activation);
        }
    }

    /**
     * We should change the activation status using this method.
     * @param activation activation that we want change its status
     * @param status target status
     */
    private void setStatus(Activation activation, String status) {
        mongoService.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        mongoService.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.activationCol"));

        String query = "{\n" +
                "\t\"findQuery\": {\n" +
                "\t\"creditId\": \"" + activation.getCreditId() + "\"\n" +
                "\t},\n" +
                "\t\"updateQuery\": {\n" +
                "\t\"$set\": {\n" +
                "\t\"status\": \"" + status + "\"\n" +
                "\t}\n" +
                "\t}\n" +
                "}";
        mongoService.updateOne(query);
    }
}
