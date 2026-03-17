package com.mydigipay.los.ruleautomation.repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mydigipay.los.ruleautomation.model.activation.Activation;
import org.springframework.stereotype.Repository;

@Repository
/*
 * Author: f.bahramnejad
 */
public interface ActivationRepository extends MongoRepository<Activation, String>{
    Activation findByCreditId(String creditId);

}
