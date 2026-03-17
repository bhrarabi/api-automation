package com.mydigipay.los.ruleautomation.model.steps.cheque.pojo;

public enum Relative {
    MYSELF(0), MOTHER(1), FATHER(2), SISTER(3), BROTHER(4), WIFE(5), CHILD(6);

    private final Integer value;

    Relative(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
