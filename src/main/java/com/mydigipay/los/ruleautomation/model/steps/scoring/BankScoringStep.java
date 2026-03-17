package com.mydigipay.los.ruleautomation.model.steps.scoring;

import com.mydigipay.los.ruleautomation.exception.ProfileException;
import com.mydigipay.los.ruleautomation.exception.ScoringException;
import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.scoring.pojo.ScoringStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.stepservices.scoring.ScoringService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Service
/*
 * Author: f.bahramnejad
 */
public class BankScoringStep extends Step {

    final
    ScoringService scoringService;

    public BankScoringStep(ScoringService scoringService, StepService service) {
        super.setStepService(service);
        this.scoringService = scoringService;
        setCode(StepCode.BANK_SCORE_WITHOUT_PAY);
    }

    @Override
    public void preconditionPreparation() throws ScoringException {

        setPreviousStepCompleteStatus();
        scoringService.checkUserEligibility(getActivation(), getUser());

    }

    @Override
    public Boolean checkPostConditions() {

        return checkStatusInJournal(ScoringStatus.BANK_SCORING_WP_PASSED.name());
    }

    @Override
    public void process() throws ProfileException, InterruptedException, ScoringException {

        if (getProcessType().equals("USER_PROCESS")) {
            preconditionPreparation();
            scoringService.processScoring(getActivation(), getUser());

        } else {
            List<String> finalStatuses = new ArrayList<>();
            finalStatuses.add(ScoringStatus.BANK_SCORING_WP_FAILED.name());
            finalStatuses.add(ScoringStatus.BANK_SCORING_WP_PASSED.name());
            setPreviousStepCompleteStatus();
            autoProcess(getActivation(), finalStatuses);
        }

    }

    @Override
    public String getCompleteStatus() {
        return "BANK_SCORING_WP_PASSED";
    }

    @Override
    public void setTestData(Map<String, Object> testData) {
    scoringService.setBankScore(Integer.parseInt(String.valueOf(testData.get("bankScore"))));
    scoringService.setIcsScore(Integer.parseInt(String.valueOf(testData.get("icsScore"))));
    }
}
