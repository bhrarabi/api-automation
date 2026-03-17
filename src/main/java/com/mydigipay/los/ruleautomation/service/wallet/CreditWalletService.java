package com.mydigipay.los.ruleautomation.service.wallet;

import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.model.wallet.CreditWallet;
import com.mydigipay.los.ruleautomation.service.JsonController;
import com.mydigipay.los.ruleautomation.utils.common.ConfigUtil;
import com.mydigipay.los.ruleautomation.utils.common.RestUtil;
import com.mydigipay.los.ruleautomation.utils.constatnts.HttpHeader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CreditWalletService {
    final
    ConfigUtil configUtil;

    public CreditWalletService(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    private RestUtil prepareRestUtilInstance(User user) {
        RestUtil request = new RestUtil(configUtil.getProperty("staging.credit-base-url-ms"));
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        headers.put("agent", HttpHeader.agent);
        return request.headers(headers);
    }

    public List<CreditWallet> getListOfWallets(User user) throws IOException {
        RestUtil restReq = prepareRestUtilInstance(user).path("wallets").get();
        if (restReq.getStatusCode().equals(200)) {
            String resp = restReq.get().getApiResponseAsString();
            return JsonController.jsonStringToPojoListUsingKey(resp, "creditWallets", CreditWallet.class);
        }
        return null;
    }
}
