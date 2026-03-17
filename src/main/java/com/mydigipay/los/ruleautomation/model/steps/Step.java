package com.mydigipay.los.ruleautomation.model.steps;

import com.mydigipay.los.ruleautomation.exception.SamatException;
import com.mydigipay.los.ruleautomation.exception.ScoringException;
import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.activation.pojo.ActivationStep;
import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

@Data
/*
 * Author: f.bahramnejad
 */
public abstract class Step {

    @Autowired
    FetchActivation fetch;

    @Autowired
    StepService stepService;

    private Activation activation;

    private User user;

    private StepCode code;

    public abstract void preconditionPreparation() throws ScoringException;

    public abstract Boolean checkPostConditions() throws SamatException;

    public abstract void process() throws Exception;

    public abstract String getCompleteStatus();

    public abstract void setTestData(Map<String, Object> testData) throws ParseException;

    public String getProcessType() {
        activation = fetch.fetchByCreditId(activation.getCreditId());
        List<ActivationStep> steps = activation.getSteps();
        for (ActivationStep step : steps) {
            if (Objects.equals(step.getCode(), this.code)) {
                return step.getProcessType();
            }
        }
        return null;
    }

    public String getStatus() {
        activation = fetch.fetchByCreditId(activation.getCreditId());
        return activation.getStatus();
    }
    public boolean checkStatusInJournal(String status){
        activation = fetch.fetchByCreditId(activation.getCreditId());
        return activation.checkStatusInJournal(status);


    }

    private void setCompleteStatus(String code) {
        String status = getCompleteStatus();
        stepService.setStatus(activation.getCreditId(), status);
        stepService.setCompleteDate(activation.getCreditId(),code);
    }

    private StepCode getPreviousStepCode() {
        List<ActivationStep> steps = activation.getSteps();
        ListIterator<ActivationStep> iterator = steps.listIterator();
        while (iterator.hasNext()) {
            ActivationStep current = iterator.next();

            if (current.getCode().equals(getCode())) {
                if (iterator.hasPrevious()) {

                    iterator.previous();
                    if (iterator.hasPrevious()){
                    return iterator.previous().getCode();}
                    else{
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public void setPreviousStepCompleteStatus() {

        StepCode code = getPreviousStepCode();
        if( code != null & code != StepCode.REGISTER){
        Step previous = stepService.findStepByCode(code.name());
        //String currentStatus = getStatus();
        String previousCompleteStatus = previous.getCompleteStatus();
        if (!checkStatusInJournal(previousCompleteStatus)) {
            previous.setActivation(activation);
            previous.setCompleteStatus(code.name());
        }}
    }
    public String getPreviouseStemCompleteStatus(){
        StepCode code = getPreviousStepCode();
        assert code != null;
        Step previous = stepService.findStepByCode(code.name());
           return previous.getCompleteStatus();
    }

    public void autoProcess(Activation activation, List<String> statuses) throws InterruptedException {
        boolean checkJournalState = false;
        do {

            Thread.sleep(30000);
            activation = fetch.fetchByCreditId(activation.getCreditId());
            for (String status : statuses) {
                checkJournalState = checkJournalState | checkStatusInJournal(status);
            }

        }
        while (!checkJournalState);

    }


}
