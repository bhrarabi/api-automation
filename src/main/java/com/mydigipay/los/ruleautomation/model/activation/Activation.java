package com.mydigipay.los.ruleautomation.model.activation;

import com.mydigipay.los.ruleautomation.model.activation.pojo.ActivationGroup;
import com.mydigipay.los.ruleautomation.model.activation.pojo.ActivationState;
import com.mydigipay.los.ruleautomation.model.activation.pojo.ActivationStep;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document(collection = "activations")
/*
 * Author: f.bahramnejad
 */
public class Activation {
    @Id
    String creditId;
    String status;
    String cellNumber;
    ActivationGroup group;
    List<ActivationStep> steps;
    List<ActivationState> journalState;
    int installmentCount;
    long initialBalance;

    @Tolerate
    public boolean checkStatusInJournal(String status) {
        List<ActivationState> states = this.getJournalState();
        return states.stream().anyMatch(p -> p.getToStatus().equals(status));

    }

    @Tolerate
    public ActivationStep findStepByCode(String code) {
        List<ActivationStep> steps = getSteps();
        for (ActivationStep current : steps) {
            if (current.getCode().name().equals(code)) {
                return current;
            }
        }
        return null;
    }
}