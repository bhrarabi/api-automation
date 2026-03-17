package com.mydigipay.los.ruleautomation.model.steps.digitalSignature;

import com.mydigipay.los.ruleautomation.exception.SamatException;
import com.mydigipay.los.ruleautomation.exception.ScoringException;
import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.digitalSignature.pojo.DigitalSignatureStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;

import java.util.Map;

public class DigitalSignatureStep extends Step {
    @Override
    public void preconditionPreparation() throws ScoringException {

    }

    public DigitalSignatureStep(StepService service) {
        this.setCode(StepCode.DIGITAL_SIGNATURE);

        super.setStepService(service);
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
        return DigitalSignatureStatus.DIGITAL_SIGNATURE_COMPLETED.name();
    }

    @Override
    public void setTestData(Map<String, Object> testData) {

    }
}
