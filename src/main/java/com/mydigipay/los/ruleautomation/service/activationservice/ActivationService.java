package com.mydigipay.los.ruleautomation.service.activationservice;

import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Data
@Service
/*
 * Author: f.bahramnejad
 */
public class ActivationService {
    final ConfigUtil configUtil;

    private Map<String, String> headers = new HashMap<>();
    private RestUtil request;
    private Activation activation;
    private User user;

    @Autowired
    public ActivationService(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    private void setupRequest() {
        request = RestUtil.init(configUtil.getProperty("staging.credit-base-url-ms"));
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        headers.put("agent", HttpHeader.agent);
    }

    public JsonPath getActivationRequest() {
        this.setupRequest();
        request = RestUtil.init(configUtil.getProperty("staging.credit-base-url-ms"))
                .headers(headers)
                .pathParam("fundProviderCode", activation.getGroup().getFundProvider().get("fpCode").toString())
                .pathParam("creditId", activation.getCreditId())
                .path("/activations/{fundProviderCode}/{creditId}")
                .get();
        return request.getApiResponseAsJson();
    }
}
