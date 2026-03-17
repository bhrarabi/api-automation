package com.mydigipay.los.ruleautomation.model.activation.pojo;

public enum StepCode {
    REGISTER(2),
    DIGIPAY_SCORE(3),
    BANK_SCORE(4),
    PROFILE(5),
    UPLOAD(6),
    WALLET_ACTIVATION(7),
    CHEQUE_UPLOAD(8),
    CLOSE(9),
    OPENING_BANK_ACCOUNT(10),
    OFFLINE_CONTRACT(11),
    BANK_SCORE_WITHOUT_PAY(12),
    FILING_PAYMENT(13),
    ALLOCATION_PREPAYMENT(14),
    BANK_ACCOUNT_VERIFICATION(15),
    DIGITAL_SIGNATURE_AND_ONLINE_CONTRACT(16),
    DIGITAL_SIGNATURE(17),
    SIGNING_DOCUMENT(18),
    E_NOTE(19),
    ARCHIVE(20),
    ACCOUNT_BLOCKING(21),
    SUSPEND(22),
    CHECK_CREDIT_FILE(23);

    private final Integer value;

    StepCode(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String getNameByCode(int code){
        for(StepCode e : StepCode.values()){
            if(code == e.value) return e.name();
        }
        return null;
    }
}
