package com.mydigipay.los.ruleautomation.model.steps.fillingPayment;

import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.fillingPayment.pojo.FillingPaymentStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.stepservices.fillingPayment.FillingPaymentService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;

@Data
@Service
/*
 * Author: b.arabi
 */
public class FillingPaymentStep extends Step {
    final FillingPaymentService fillingPaymentService;

    public FillingPaymentStep(FillingPaymentService fillingPaymentService, StepService service) {
        this.fillingPaymentService = fillingPaymentService;
        this.setCode(StepCode.FILING_PAYMENT);
        super.setStepService(service);
    }

    @Override
    public void preconditionPreparation() {
        fillingPaymentService.setActivation(super.getActivation());
        fillingPaymentService.setUser(super.getUser());
        fillingPaymentService.setupRequest();
        setPreviousStepCompleteStatus();

    }

    @Override
    public Boolean checkPostConditions() {
        return getStatus().equals(FillingPaymentStatus.FILING_PAYMENT_SUCCEED.name());
    }

    @Override
    public void process() {
        this.preconditionPreparation();
        fillingPaymentService.fillingPayment();
    }

    @Override
    public String getCompleteStatus() {
        return FillingPaymentStatus.FILING_PAYMENT_SUCCEED.name();
    }

    @Override
    public void setTestData(Map<String, Object> testData) {

    }
}
