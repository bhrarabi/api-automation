package com.mydigipay.los.ruleautomation.service.activationservice;

import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.repository.ActivationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/*
 * Author: f.bahramnejad
 */
public class FetchActivation {
    @Autowired
    ActivationRepository activationRepo;

    /**
     * These methods find an activation via credit id.
     * @param creditId Activation creditId
     * @return updated activation
     */
    public Activation fetchByCreditId(String creditId) {
        return activationRepo.findByCreditId(creditId);
    }
}
