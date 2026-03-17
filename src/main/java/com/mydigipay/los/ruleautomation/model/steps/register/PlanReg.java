package com.mydigipay.los.ruleautomation.model.steps.register;

import com.mydigipay.los.ruleautomation.exception.RegisterException;
import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.service.stepservices.register.RegisterService;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class PlanReg implements RegisterType {
    final
    FetchActivation fetch;
    final
    RegisterService regService;
    //private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    private String groupId;
    private User user;

    public PlanReg(FetchActivation fetch, RegisterService regService) {

        this.fetch = fetch;
        this.regService = regService;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public Activation registerUser() throws RegisterException, ParseException {
        String creditId = regService.registerByPlan(groupId, user);
        return fetch.fetchByCreditId(creditId);

    }
}
