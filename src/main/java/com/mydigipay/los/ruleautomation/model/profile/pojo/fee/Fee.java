package com.mydigipay.los.ruleautomation.model.profile.pojo.fee;

import lombok.Getter;

@Getter
public class Fee {
    FeeType type;
    double value;
    PayType payType;
    SettleType settlementType;
}
