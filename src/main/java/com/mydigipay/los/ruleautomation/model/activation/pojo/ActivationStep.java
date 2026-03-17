package com.mydigipay.los.ruleautomation.model.activation.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public class ActivationStep {
    StepCode code;
    String option;
    String processType;
    Map<String, String> additionalInfo;
    String stepResult;

    long startedDate;
    @Setter
    long completedDate;
}
