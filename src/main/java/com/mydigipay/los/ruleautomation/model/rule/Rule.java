package com.mydigipay.los.ruleautomation.model.rule;


import com.mydigipay.los.ruleautomation.model.rule.pojo.*;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "rules")
/*
 * Author: b.arabi
 */
public class Rule {
    @Id
    public ObjectId id;
    private String ruleId;
    private ProviderModel fundProvider;
    private String customerType;
    private Short activationOffset;
    private Short expirationOffsetFromActivation;
    private Short expirationOffset;
    private Short registerOffset;
    private String type;
    private Map<String, Integer> installmentCount;
    private Boolean installmentPayable;
    private Short installmentOffset;
    private Map<String, Integer> balance;
    private Double interestPercentage;
    private Double prepaymentPercentage;
    private Double feeChargePercentage;
    private Integer bankAcceptableScore;
    private Integer dpScoreResult;
    private List<Relative> chequeRelativeSupport;
    private String closeType;
    //    private String group;
    private Double DpScore;
    private Boolean isNeedToDpScore;
    private Integer integrateScoreVersioningMatrix;
    private Integer dpIntegrateScoreVersioningMatrix;
    private Integer bnplScoreVersioningMatrix;
    private List<SupportedCollateralType> supportedCollateralTypes;
    private List<Step> steps;
    private List<ProfileItem> profileItems;
    private String profileId;
    private String groupTitle;
    private boolean automaticAllocation;
    private String crn;
    private Integer serviceType;
}
