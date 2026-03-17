package com.mydigipay.los.ruleautomation.model.steps.checkCreditFile;
import com.mydigipay.los.ruleautomation.exception.SamatException;
import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.checkCreditFile.pojo.SamatStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.stepservices.checkCreditFile.SamatService;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Component
public class SamatStep extends Step
{
    final SamatService samatService;

    public SamatStep(SamatService samatService , StepService service ) {
        this.samatService = samatService;
        super.setStepService(service);
        setCode(StepCode.CHECK_CREDIT_FILE);
    }

    @Override
    public void preconditionPreparation() {
        setPreviousStepCompleteStatus();

    }

    @Override
    public Boolean checkPostConditions() throws SamatException {
        if (getStatus().equals(SamatStatus.CHECK_CREDIT_FILE_RETRY.name())){
            throw new SamatException("Samat service is unavailable!");
        }

        return checkStatusInJournal(SamatStatus.CHECK_CREDIT_FILE_SUCCESS.name());
    }

    @Override
    public void process() throws Exception {
        if (getProcessType().equals("USER_PROCESS")) {
            preconditionPreparation();
            samatService.SamatInquiry(getActivation(),getUser());


        } else {
            List<String> finalStatuses = new ArrayList<>();
            finalStatuses.add(SamatStatus.CHECK_CREDIT_FILE_SUCCESS.name());
            finalStatuses.add(SamatStatus.CHECK_CREDIT_FILE_REJECTED.name());
            setPreviousStepCompleteStatus();
            autoProcess(getActivation(), finalStatuses);
        }


    }

    @Override
    public String getCompleteStatus() {

        return SamatStatus.CHECK_CREDIT_FILE_SUCCESS.name();
    }

    @Override
    public void setTestData(Map<String, Object> testData) {

    }
}
