package com.mydigipay.los.ruleautomation.repository;

import com.mydigipay.los.ruleautomation.model.profile.OnboardingProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditProfileRepository extends MongoRepository<OnboardingProfile, String> {
    OnboardingProfile findByProfileId(String profileId);
}
