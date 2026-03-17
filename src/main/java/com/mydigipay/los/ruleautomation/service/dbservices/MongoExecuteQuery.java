package com.mydigipay.los.ruleautomation.service.dbservices;

import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;

import io.restassured.response.Response;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Data
@Service
/*
 * Author: f.bahramnejad
 */

public class MongoExecuteQuery {

    @Value("${staging.mongo-db-base-url}")
    private String mongoBaseUrl;
    private String dbName;
    private String collectionName;

    private RestUtil restReq;


    private void prepareRestUtil() {
        restReq = new RestUtil(mongoBaseUrl);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", HttpHeader.contentType);
        restReq = restReq.headers(headers);
    }

    /**
     * Find query function.
     * @param conditions query conditions
     * @return result of query
     */
    public Response find(Object conditions) {
        prepareRestUtil();
        restReq.path("/find/" + dbName + "/" + collectionName).body(conditions).post();
        return restReq.getResponse();

    }

    /**
     * UpdateOne query function.
     *
     * @param conditions query conditions
     */
    public void updateOne(Object conditions) {
        prepareRestUtil();
        restReq.path("/updateOne/" + dbName + "/" + collectionName).body(conditions).put();
    }

    /**
     * InsertOne query function.
     * @param conditions query conditions
     */
    public void insertOne(Object conditions) {
        prepareRestUtil();
        restReq.path("/insertOne/" + dbName + "/" + collectionName).body(conditions).post();
    }

}
