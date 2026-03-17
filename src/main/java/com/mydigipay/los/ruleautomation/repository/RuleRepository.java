package com.mydigipay.los.ruleautomation.repository;

import com.mydigipay.los.ruleautomation.model.rule.Rule;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
/**
 * Author: b.arabi
 */
public interface RuleRepository extends MongoRepository<Rule, ObjectId> {
    Rule findByRuleId(String ruleId);

}
