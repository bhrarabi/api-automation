package com.mydigipay.los.ruleautomation.model.plan;

import com.mydigipay.los.ruleautomation.model.plan.pojo.Collateral;
import com.mydigipay.los.ruleautomation.model.plan.pojo.FundProvider;
import com.mydigipay.los.ruleautomation.model.plan.pojo.PlanDetail;
import com.mydigipay.los.ruleautomation.model.plan.pojo.PlanRegistrationFlow;
import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelCellRange;
import lombok.Data;

import java.util.List;


@Data
public class Plan {
    @ExcelCellName("sumInstallmentAmount")
    private long sumInstallmentAmount;
    @ExcelCellRange
    private FundProvider fundProvider;
    @ExcelCellName("interestPercentage")
    private double interestPercentage;
    @ExcelCellName("installmentAmount")
    private long installmentAmount;
    @ExcelCellName("groupId")
    private String groupId;
    @ExcelCellName("preRegisterWithDelay")
    private boolean preRegisterWithDelay;
    @ExcelCellName("filingPaymentAmount")
    private String filingPaymentAmount;
    @ExcelCellName("active")
    private boolean active;
    private PlanRegistrationFlow planRegistrationFlowDto;
    @ExcelCellRange
    private Collateral collateralDto;
    @ExcelCellName("allocationPrepaymentPercentage")
    private double allocationPrepaymentPercentage;
    @ExcelCellName("payableAmount")
    private long payableAmount;
    @ExcelCellName("installmentCount")
    private long installmentCount;
    @ExcelCellName("collateralAmount")
    private long collateralAmount;
    @ExcelCellName("allocationPrepaymentAmount")
    private long allocationPrepaymentAmount;
    private List<PlanDetail> details;
    @ExcelCellName("planId")
    private String planId;
    @ExcelCellName("hasAllocationPrepayment")
    private boolean hasAllocationPrepayment;
    @ExcelCellName("creditAmount")
    private long creditAmount;
    @ExcelCellName("serviceType")
    private int serviceType;

}
