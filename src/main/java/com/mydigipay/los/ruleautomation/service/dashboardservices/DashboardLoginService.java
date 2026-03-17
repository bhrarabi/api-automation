package com.mydigipay.los.ruleautomation.service.dashboardservices;

import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import io.restassured.path.json.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
/*
 * Author: b.arabi
 */
public class DashboardLoginService {

    @Value("${staging.dashboard-base-url}")
    String dashboardBaseUrl;

    private final Map<String, String> headers = new HashMap<>();

    public DashboardLoginService() {
        headers.put("Zone", HttpHeader.zone);
        headers.put("Content-type", HttpHeader.formParam);
        headers.put("User-Agent", HttpHeader.userAgent);
        headers.put("Authorization", HttpHeader.loginDashboardAuthorization);

    }

    /**
     * Login to dashboard
     * @return
     */
    public String getToken() {

        RestUtil instance = RestUtil.init(dashboardBaseUrl)
                .headers(headers)
                .path("oauth/token")
                .formParam("username", "test-staff-user")
                .formParam("password", "testQWE!@#")
                .formParam("grant_type", "password")
                .post();
        JsonPath response = instance.getApiResponseAsJson();
        return response.getString("access_token");
    }
}
