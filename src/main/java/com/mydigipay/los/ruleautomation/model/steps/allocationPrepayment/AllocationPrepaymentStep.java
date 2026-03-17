package com.mydigipay.los.ruleautomation.model.steps.allocationPrepayment;

import com.mydigipay.los.ruleautomation.exception.SamatException;
import com.mydigipay.los.ruleautomation.exception.ScoringException;
import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.allocationPrepayment.pojo.AllocationPrerpaymentStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.stepservices.alloctionPrepayment.AllocatioPrepaymentService;
import lombok.Data;
import org.springframework.stereotype.Service;
import java.util.Map;

@Data
@Service
public class AllocationPrepaymentStep extends Step {
    final AllocatioPrepaymentService allocationPrepaymentService;
    private String contractNumber;
    private boolean autoAllocation;

    public AllocationPrepaymentStep(AllocatioPrepaymentService allocationPrepaymentService, StepService service) {
        super.setStepService(service);
        this.allocationPrepaymentService = allocationPrepaymentService;
        setCode(StepCode.ALLOCATION_PREPAYMENT);
    }

    @Override
    public void preconditionPreparation() throws ScoringException {
        setPreviousStepCompleteStatus();
        allocationPrepaymentService.completePreviousSteps(getActivation());
    }

    @Override
    public Boolean checkPostConditions() throws SamatException {
        return checkStatusInJournal(AllocationPrerpaymentStatus.ALLOCATION_PREPAYMENT_ACCEPTED.name());
    }

    @Override
    public void process() throws Exception {
        preconditionPreparation();
        if ((allocationPrepaymentService.checkAllocationPrepaymentAmount(getUser(), getActivation()))) {
            super.getStepService().setStatus(getActivation().getCreditId(), AllocationPrerpaymentStatus.ALLOCATION_PREPAYMENT_READY_TO_APPROVE.name());
          }
            if (autoAllocation) {
                allocationPrepaymentService.automaticAllocation(getActivation());
            } else {

                allocationPrepaymentService.changeAllocationStatus(getActivation());
                allocationPrepaymentService.importAllocationFile(getUser(), getActivation(), contractNumber);
            }
        }



    @Override
    public String getCompleteStatus() {
        return AllocationPrerpaymentStatus.ALLOCATION_PREPAYMENT_ACCEPTED.name();
    }

    @Override
    public void setTestData(Map<String, Object> testData) {
        setContractNumber((String) testData.get("ContractNumber"));
        setAutoAllocation((boolean) testData.get("AutoAllocation"));
    }
}
