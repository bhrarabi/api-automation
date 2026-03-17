package com.mydigipay.los.ruleautomation.service.dbservices;

import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SqlExecuteQuery {

    public String query;
    public RestUtil restUtil;
    @Value("${staging.sql-db-base-url}")
    private String creditSqlRestBaseUrl;

    /**
     * We should use these APIs to execute SQL queries and retrieve data.
     *
     * @return Result of the query.
     */
    private RestUtil setSpec() {
        restUtil = new RestUtil(creditSqlRestBaseUrl);
        Map<String, String> headers = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        body.put("query", this.query);
        headers.put("Content-Type", HttpHeader.contentType);
        return restUtil.headers(headers).path("/exec").body(body);
    }

    public RestUtil execute(String query) {
        this.query = query;
        return setSpec().post();

    }

}
