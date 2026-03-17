package com.mydigipay.los.ruleautomation.service.stepservices.fillingPayment;

import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.steps.fillingPayment.pojo.FillingPaymentRequestModel;
import com.mydigipay.los.ruleautomation.model.steps.fillingPayment.pojo.InitPaymentRequestModel;
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
public class FillingPaymentService {

    ConfigUtil configUtil;
    FetchActivation fetch;
    private Map<String, String> headers = new HashMap<>();
    private RestUtil request;
    private Activation activation;
    private User user;
    private InitPaymentRequestModel initPaymentRequestModel;
    private FillingPaymentRequestModel fillingPaymentRequestModel;


    public FillingPaymentService(ConfigUtil configUtil, FetchActivation fetch) {
        this.configUtil = configUtil;
        this.fetch = fetch;
    }

    public void setupRequest() {
        headers.put("User-Id", user.getUserId());
        headers.put("Content-Type", HttpHeader.contentType);
        headers.put("Digipay-Version", HttpHeader.digipayVersion);
        headers.put("agent", HttpHeader.agent);
        initPaymentRequestModel = new InitPaymentRequestModel();
        initPaymentRequestModel.setCreditId(activation.getCreditId());
        initPaymentRequestModel.setFundProviderCode(activation.getGroup().getFundProvider().get("fpCode").toString());

        fillingPaymentRequestModel = new FillingPaymentRequestModel();
        fillingPaymentRequestModel.setType("wallet");

    }

    public void initPayment() {
        request = RestUtil.init(configUtil.getProperty("staging.credit-base-url-ms"))
                .headers(headers)
                .body(initPaymentRequestModel)
                .path("/payments/init")
                .post();
        fillingPaymentRequestModel.setTicket(request.getApiResponseAsJson().getString("ticket"));
    }

    public void fillingPayment() {
        this.initPayment();
        request = RestUtil.init(configUtil.getProperty("staging.credit-payment-base-url-ms"))
                .headers(headers)
                .body(fillingPaymentRequestModel)
                .path("/filings/pay/wallet")
                .post();
    }


}
