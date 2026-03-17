package com.mydigipay.los.ruleautomation.model.rule.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class ProviderModel {

    private String businessId;
    private Date activationTime;
    private Date modificationTime;
    private String name;
    private String crn; // Company Registration Number
    private String code;
    private Integer fpCode;

}
