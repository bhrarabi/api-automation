package com.mydigipay.los.ruleautomation.model.steps.cheque.pojo;

import lombok.Data;

@Data
public class ChequeAdminRequestModel {
    private String creditId;
    private String stepTag;
    private String fundProviderCode;
    private String userId;
}
