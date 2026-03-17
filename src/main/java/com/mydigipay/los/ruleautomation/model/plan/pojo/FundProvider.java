package com.mydigipay.los.ruleautomation.model.plan.pojo;

import com.poiji.annotation.ExcelCellName;
import lombok.Data;

@Data
public class FundProvider {
    @ExcelCellName("fundProvider.code")
    public String fundProviderCode;
    @ExcelCellName("fundProvider.name")
    public String name;
    @ExcelCellName("fundProvider.active")
    public boolean active;
    public String icon;
    public String color;

  
}
