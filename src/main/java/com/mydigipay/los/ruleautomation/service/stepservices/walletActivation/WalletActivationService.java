package com.mydigipay.los.ruleautomation.service.stepservices.walletActivation;

import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WalletActivationService {
    final
    ConfigUtil configUtil;
    final
    FetchActivation fetch;

    public WalletActivationService(ConfigUtil configUtil, FetchActivation fetch) {
        this.configUtil = configUtil;
        this.fetch = fetch;
    }

    private RestUtil prepareRestUtilInstance(User user) {
        RestUtil request = new RestUtil(configUtil.getProperty("staging.installment-base-url-ms"));
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        headers.put("agent", HttpHeader.agent);
        return request.headers(headers);
    }

    public int contractSummaryGetStatus(Activation activation, User user) {

        return prepareRestUtilInstance(user).pathParam("creditId", activation.getCreditId())
                .path("contracts/summary/{creditId}").get()
                .getStatusCode();

    }

}
