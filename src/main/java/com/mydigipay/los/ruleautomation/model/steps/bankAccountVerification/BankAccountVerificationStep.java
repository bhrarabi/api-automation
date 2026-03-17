package com.mydigipay.los.ruleautomation.model.steps.bankAccountVerification;


import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.bankAccountVerification.pojo.BankAccountVerificationStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.stepservices.bankAccountVerification.BankAccountVerificationService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;

@Data
@Service
/*
 * Author: b.arabi
 */
public class BankAccountVerificationStep extends Step {

    BankAccountVerificationService bankAccountVerificationService;
    public BankAccountVerificationStep(BankAccountVerificationService bankAccountVerificationService, StepService service){
        setCode(StepCode.BANK_ACCOUNT_VERIFICATION);
        this.bankAccountVerificationService = bankAccountVerificationService;
        super.setStepService(service);
    }

    @Override
    public void preconditionPreparation() {
        bankAccountVerificationService.setActivation(super.getActivation());
        bankAccountVerificationService.setUser(super.getUser());
        bankAccountVerificationService.setupRequest();
    }

    @Override
    public Boolean checkPostConditions() {
        return getStatus().equals(getCompleteStatus());
    }

    @Override
    public void process() throws Exception {
        this.preconditionPreparation();
        String status;
        do{
            status = bankAccountVerificationService.getBankAccountStatus();
        }while (status.equals("1"));  //To check whether the status is pending or not.

    }

    @Override
    public String getCompleteStatus() {
        return BankAccountVerificationStatus.BANK_ACCOUNT_VERIFICATION_SUCCEED.name();
    }

    @Override
    public void setTestData(Map<String, Object> testData) {

    }
}
