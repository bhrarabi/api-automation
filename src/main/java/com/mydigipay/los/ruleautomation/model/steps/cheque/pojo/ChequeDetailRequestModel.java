package com.mydigipay.los.ruleautomation.model.steps.cheque.pojo;

import lombok.Data;

@Data
public class ChequeDetailRequestModel {
    private String date;
    private String iban="";
    private Long amount;
    private String bankName="";
    private String chequeId;
    private String ownerName;
    private String ownerNationalCode;
    private Integer ownerRelative=0;
    private Integer chequeVersion=1;
    private long ownerBirthDate;
    private String ownerCellNumber;

}
