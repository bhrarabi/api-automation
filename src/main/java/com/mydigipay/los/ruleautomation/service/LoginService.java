package com.mydigipay.los.ruleautomation.service;

import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginService {

    @Value("${staging.app-base-url}")
    String baseUrl;

    @Value("${staging.redis-url}")
    String redisUrl;

    Map<String, String> headers = new HashMap<>();

    String userId;
    String OTP;


    String body = "{\n" +
            "                        \"cellNumber\": \"09394000260\",\n" +
            "                        \"device\": {\n" +
            "                            \"deviceId\": \"6a187fb6-6185-46a2-99b1-1d2889cd5085\",\n" +
            "                            \"deviceModel\": \"WEB_BROWSER\",\n" +
            "                            \"deviceAPI\": \"WEB_BROWSER\",\n" +
            "                            \"osName\": \"WEB\"\n" +
            "                        }\n" +
            "        }";

    public LoginService() {
        headers.put("Accept", HttpHeader.accept);
        headers.put("Agent", HttpHeader.agent);
        headers.put("Content-type", HttpHeader.contentType);
        headers.put("User-Agent", HttpHeader.userAgent);
    }

    public String sendSMS() {
        headers.put("Authorization", HttpHeader.loginAppAuthorization);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        RestUtil instance = RestUtil.init(baseUrl)
                .headers(headers)
                .body(body)
                .path("/users/send-sms")
                .post();
        userId = instance.getApiResponseAsJson().getString("userId");
        return userId;
    }

    public void getOtp() {
        String userIdParam = "auth_otp_" + sendSMS();
        headers.remove("Authorization");
        headers.remove("Digipay-Version");
        RestUtil instance = RestUtil.init(redisUrl)
                .headers(headers)
                .path("/redis/find/{userId}")
                .pathParam("userId", userIdParam)
                .get();
        List<String> otpCodes = instance.getApiResponseAsJson().getList("value.otpCodes[1].value");
        OTP = otpCodes.get(otpCodes.size() - 1);
    }

    public String getToken() {
        getOtp();
        body = "{\n" +
                "    \"smsToken\": \"" + OTP + "\",\n" +
                "    \"userId\": \"" + userId + "\"\n" +
                "}";
        headers.put("Authorization", HttpHeader.loginAppAuthorization);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        RestUtil instance = RestUtil.init(baseUrl)
                .headers(headers)
                .body(body)
                .path("/users/activate")
                .post();
        return instance.getApiResponseAsJson().getString("accessToken");
    }

    public String loginWithPassword() {
        sendSMS();
        String password = "1234";
        body = "{\n" +
                "    \"password\": \"" + password + "\",\n" +
                "    \"username\": \"" + userId + "\"\n" +
                "}";
        RestUtil instance = RestUtil.init(baseUrl)
                .headers(headers)
                .body(body)
                .path("/users/login")
                .post();
        return instance.getApiResponseAsJson().getString("accessToken");
    }
}
