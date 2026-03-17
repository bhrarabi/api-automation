package com.mydigipay.los.ruleautomation.model.steps.register.pojo;

import lombok.Getter;

@Getter
public enum RegisterStatus {
    REGISTERED("REGISTERED");
    private final String status;

    RegisterStatus(String status) {
        this.status=status;
    }
}
