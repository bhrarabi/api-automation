package com.mydigipay.los.ruleautomation.model.activation.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class ActivationGroup {
    Map<String, Object> fundProvider;
    String ruleId;
}
