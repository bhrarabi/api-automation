package com.mydigipay.los.ruleautomation.model.steps.cheque.pojo;


import lombok.Getter;

@Getter
public enum ChequeVersion {
    OLD(0), NEW(1);
    private final int value;
    ChequeVersion(int value) {
        this.value = value;
    }

}
