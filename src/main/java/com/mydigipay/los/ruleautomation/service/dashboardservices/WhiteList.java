package com.mydigipay.los.ruleautomation.service.dashboardservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mydigipay.los.ruleautomation.service.dbservices.MongoExecuteQuery;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;


@Service
@Lazy
public class WhiteList {
    @Autowired
    ConfigUtil configUtil;

    @Autowired
    MongoExecuteQuery executeQuery;

    /**
     * Adding a list of cellNumbers to whitelist of a specific group
     * @param groupId The whitelist group
     * @param cellNumbers list of cell numbers which we want to add to whitelist
     */
    public void addToWhitelist(String groupId, List<String> cellNumbers) {
        RestUtil addToListReq = new RestUtil(configUtil.getProperty("staging.credit-base-url-ms"));
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", HttpHeader.adminUserId);
        headers.put("Content-Type", HttpHeader.contentType);
        JSONObject body = new JSONObject();
        body.put("groupId", groupId);
        body.put("cellNumbers", cellNumbers);
        addToListReq.headers(headers).path("/admin/whitelist/import").body(body).post();


    }

    /**
     * Adding only one cell number to whitelist of a specific group
     * @param groupId The whitelist group
     * @param cellNumber cell number which we want check
     * @return found
     */
    private String waitForAddingToWhiteList(String groupId, String cellNumber) {
        executeQuery.setDbName(configUtil.getProperty("databases.switch-credit.name"));
        executeQuery.setCollectionName(configUtil.getProperty("databases.switch-credit.collections.whitelistsCol"));
        Map<String, String> body = new HashMap<>();
        body.put("groupId", groupId);
        body.put("cellNumber", cellNumber);
        return executeQuery.find(body).print();
    }

    public void addToWhitelist(String groupId, String cellNumber) {
        List<String> cellNumbers = new ArrayList<>();
        cellNumbers.add(cellNumber);
        addToWhitelist(groupId, cellNumbers);
        String whiteListResult;
        do {
            whiteListResult = waitForAddingToWhiteList(groupId, cellNumber);
        }
        while (whiteListResult.equals("[]"));
    }

}
