package com.mydigipay.los.ruleautomation.model.address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    Location city;
    Location province;
    String address;
    String addressNo;
    String addressUnit;
    String phoneNum;
    Location birthPlace;
    String postalCode;

}
