package com.mydigipay.los.ruleautomation.model.rule.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Getter
@EqualsAndHashCode
public class Step {
    private String code;
    private Option option;
    @EqualsAndHashCode
            .Exclude
    private boolean runTest;
    private ProcessType processType = ProcessType.USER_PROCESS;


}
