package com.mydigipay.los.ruleautomation.model.profile;

import com.mydigipay.los.ruleautomation.model.profile.pojo.PaymentTerm;
import com.mydigipay.los.ruleautomation.model.serviceType.ServiceType;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "credit_profiles")
public class OnboardingProfile {
    String profileId;
    ServiceType serviceType;
    PaymentTerm paymentTerm;


}
