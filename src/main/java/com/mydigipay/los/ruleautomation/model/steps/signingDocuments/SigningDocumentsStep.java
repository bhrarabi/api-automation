package com.mydigipay.los.ruleautomation.model.steps.signingDocuments;

import com.mydigipay.los.ruleautomation.exception.SamatException;
import com.mydigipay.los.ruleautomation.exception.ScoringException;
import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.signingDocuments.pojo.SigningDocumentStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;

import java.util.Map;

public class SigningDocumentsStep extends Step {
    public SigningDocumentsStep(StepService service) {
        this.setCode(StepCode.SIGNING_DOCUMENT);

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
        return SigningDocumentStatus.SIGNING_DOCUMENT_COMPLETED.name();
    }

    @Override
    public void setTestData(Map<String, Object> testData) {

    }
}
