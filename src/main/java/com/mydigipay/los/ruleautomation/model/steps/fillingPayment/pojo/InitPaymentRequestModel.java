package com.mydigipay.los.ruleautomation.model.steps.fillingPayment.pojo;

import lombok.Data;

@Data
public class InitPaymentRequestModel {
    private String creditId;
    private String fundProviderCode;
}
