package com.mydigipay.los.ruleautomation.model.plan.pojo;

import com.poiji.annotation.ExcelCellName;
import lombok.Data;

@Data
public class Collateral {
    @ExcelCellName("collateralName")
    private String name;
    @ExcelCellName("collateralType")
    private String type;
   private PlanDescription description;    

}
