package com.mydigipay.los.ruleautomation.model.profile.pojo.fee;

public enum FeeType {
    FIX_AMOUNT(0), PERCENTAGE(1);
    private final Integer value;

    FeeType(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String getNameByCode(int code){
        for(FeeType e : FeeType.values()){
            if(code == e.value) return e.name();
        }
        return null;
    }
}
