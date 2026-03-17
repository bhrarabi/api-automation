package com.mydigipay.los.ruleautomation.model.wallet;

import lombok.Data;

@Data
public class CreditWallet{
    String title;
    int fundProviderCode;
    long balance;
    String creditId;
    int serviceType;
}
