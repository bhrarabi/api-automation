package com.mydigipay.los.ruleautomation.model.profile.pojo.fee;


public enum SettleType
{
    FIRST_INSTALLMENT(0),
    ON_ALL_INSTALLMENT(1),
    ON_ALLOCATION(2),
    ON_PURCHASE(3);
    private final Integer value;

    SettleType(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String getNameByCode(int code){
        for(SettleType e : SettleType.values()){
            if(code == e.value) return e.name();
        }
        return null;
    }
}
