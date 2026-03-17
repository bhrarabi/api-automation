package com.mydigipay.los.ruleautomation.model.profile.pojo;


import com.mydigipay.los.ruleautomation.model.profile.pojo.fee.Fee;
import com.mydigipay.los.ruleautomation.model.profile.pojo.fee.FeeType;
import com.mydigipay.los.ruleautomation.model.profile.pojo.fee.SettleType;
import lombok.Getter;

import java.util.List;

@Getter
public class PaymentTerm {
    List<Fee> feeCharge;
    List<Fee> fpFeeCharge;
    public double getFeechargePercentage(SettleType settleType){
        double feePercent=0;
        for(Fee fee:feeCharge){
            if(fee.getSettlementType().equals(settleType) & fee.getType().equals(FeeType.PERCENTAGE)){
                feePercent+=fee.getValue();
            }
        }
        return feePercent;
    }
   public double getFpFeechargePercentage(SettleType settleType){
        double feePercent=0;
        for(Fee fee:fpFeeCharge){
            if(fee.getSettlementType().equals(settleType) & fee.getType().equals(FeeType.PERCENTAGE)){
                feePercent+=fee.getValue();
            }
        }
        return feePercent;
    }
}
