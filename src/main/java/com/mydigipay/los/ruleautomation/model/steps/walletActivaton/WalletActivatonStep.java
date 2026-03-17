package com.mydigipay.los.ruleautomation.model.steps.walletActivaton;

import com.mydigipay.los.ruleautomation.exception.SamatException;
import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.walletActivaton.pojo.WalletActivatonStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.stepservices.walletActivation.WalletActivationService;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
/*
 * Author: a.mirghasemi
 */
public class WalletActivatonStep extends Step
{
    final WalletActivationService activationService;

    public WalletActivatonStep( WalletActivationService activationService,StepService service) {
        this.activationService = activationService;
        super.setStepService(service);
        setCode(StepCode.WALLET_ACTIVATION);
    }

    @Override
    public void preconditionPreparation() {
    }

    @Override
    public Boolean checkPostConditions() throws SamatException {
        return getStatus().equals(WalletActivatonStatus.ACTIVE.name()) && activationService.contractSummaryGetStatus(getActivation(),getUser())==200;
    }

    @Override
    public void process() throws Exception {
       if( checkStatusInJournal(getPreviouseStemCompleteStatus())){
            List<String> finalStatuses = new ArrayList<>();
            finalStatuses.add(WalletActivatonStatus.ACTIVE.name());
            autoProcess(getActivation(), finalStatuses);
        }
    else {
        throw  new Exception("Previous step should be completed!");
       }
    }



    @Override
    public String getCompleteStatus() {
        return WalletActivatonStatus.ACTIVE.name();
    }

    @Override
    public void setTestData(Map<String, Object> testData) {

    }
}
