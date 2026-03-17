package com.mydigipay.los.ruleautomation.model.steps.promissoryNote;

import com.mydigipay.los.ruleautomation.exception.SamatException;
import com.mydigipay.los.ruleautomation.exception.ScoringException;
import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.promissoryNote.pojo.ENoteStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;

import java.util.Map;

public class ENoteStep extends Step {
    public ENoteStep(StepService service) {
        this.setCode(StepCode.E_NOTE);
        super.setStepService(service);
    }

    @Override
    public void preconditionPreparation() throws ScoringException {

    }

    @Override
    public Boolean checkPostConditions() throws SamatException {
        return null;
    }

    @Override
    public void process() throws Exception {

    }

    @Override
    public String getCompleteStatus() {
        return ENoteStatus.E_NOTE_COMPLETED.name();
    }

    @Override
    public void setTestData(Map<String, Object> testData) {

    }
}
