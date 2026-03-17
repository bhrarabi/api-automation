package com.mydigipay.los.ruleautomation.service.stepservices.bankAccountVerification;

import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.user.User;

import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Data
@Service
/*
 * Author: b.arabi
 */
public class BankAccountVerificationService {
    ConfigUtil configUtil;

    private Map<String, String> headers = new HashMap<>();
    private RestUtil request;
    private Activation activation;
    private User user;
    FetchActivation fetch;

    public BankAccountVerificationService(ConfigUtil configUtil, FetchActivation fetch) {
        this.configUtil = configUtil;
        this.fetch = fetch;
    }


    public void setupRequest() {
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        headers.put("agent", HttpHeader.agent);
    }

    public String getBankAccountStatus() {
        request = RestUtil.init(configUtil.getProperty("staging.credit-base-url-ms"))
                .headers(headers)
                .pathParam("fundProviderCode", activation.getGroup().getFundProvider().get("fpCode").toString())
                .pathParam("creditId", activation.getCreditId())
                .path("/bank-accounts/status/{fundProviderCode}/{creditId}")
                .get();
        return request.getApiResponseAsJson().getString("status");
    }
}
