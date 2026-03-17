package com.mydigipay.los.ruleautomation.model.profile.pojo.fee;

public enum PayType {
    CREDIT(0),
    CASH(1);
    private final Integer value;

    PayType(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String getNameByCode(int code){
        for(PayType e : PayType.values()){
            if(code == e.value) return e.name();
        }
        return null;
    }
}
