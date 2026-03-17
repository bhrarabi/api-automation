package com.mydigipay.los.ruleautomation.model.serviceType;

public enum ServiceType {
    BNPL(0),
    CREDIT(1),
    INSTALLMENT_SALE(2);
    private final Integer value;


    ServiceType(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
