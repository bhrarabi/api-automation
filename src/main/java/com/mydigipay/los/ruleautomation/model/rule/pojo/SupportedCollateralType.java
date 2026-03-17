package com.mydigipay.los.ruleautomation.model.rule.pojo;

import lombok.Getter;

@Getter
public enum SupportedCollateralType {
    OLD_CHEQUE(0), NEW_CHEQUE(1), E_NOTE(2), UN_PAYABLE(3), BASED_ON_SCORE(4);

    private final int value;

    SupportedCollateralType(int value) {
        this.value = value;
    }

}
