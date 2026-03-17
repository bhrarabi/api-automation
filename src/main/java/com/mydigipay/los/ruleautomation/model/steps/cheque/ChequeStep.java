package com.mydigipay.los.ruleautomation.model.steps.cheque;

import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.cheque.pojo.ChequeDetailRequestModel;
import com.mydigipay.los.ruleautomation.model.steps.cheque.pojo.ChequeStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.stepservices.cheque.ChequeService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Data
@Service
/*
 * Author: b.arabi
 */
public class ChequeStep extends Step {

    ChequeService chequeStepService;

    private ChequeDetailRequestModel chequeDetailRequestModel;

    public ChequeStep(ChequeService chequeStepService, StepService service){
        setCode(StepCode.CHEQUE_UPLOAD);
        this.chequeStepService = chequeStepService;
        super.setStepService(service);
    }


    @Override
    public void preconditionPreparation() {
        chequeStepService.setActivation(super.getActivation());
        chequeStepService.setUser(super.getUser());
        chequeStepService.setupRequest();
        setPreviousStepCompleteStatus();
    }

    @Override
    public void process(){
        this.preconditionPreparation();
        chequeStepService.postChequeDetail(chequeDetailRequestModel);
        chequeStepService.uploadDocument();
        chequeStepService.chequeConfirm();
        chequeStepService.adminChequeAccept();
        chequeStepService.adminChequeReceive();
        chequeStepService.adminChequeApprove();
    }

    @Override
    public String getCompleteStatus() {
        return ChequeStatus.CHEQUE_UPLOAD_COMPLETE.name();
    }

    @Override
    public void setTestData(Map<String, Object> testData) throws ParseException {
        setChequeDetailRequestModel(chequeStepService.setCheckDetails(getUser(), (ChequeDetailRequestModel) testData.get("ChequeDetail")));
    }

    @Override
    public Boolean checkPostConditions() {
        return getStatus().equals(ChequeStatus.CHEQUE_UPLOAD_COMPLETE.name());
    }

}
