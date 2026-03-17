package com.mydigipay.los.ruleautomation.model.steps.register;

import com.mydigipay.los.ruleautomation.exception.ImportException;
import com.mydigipay.los.ruleautomation.exception.RegisterException;
import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.rule.Rule;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.repository.GroupRepository;
import com.mydigipay.los.ruleautomation.service.dashboardservices.WhiteList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;


@Component
@Slf4j
/*
 * Author: f.bahramnejad
 */
public class RegisterStep {
    final
    GroupRepository grpRepo;
    private final PlanReg registerTypePlan;
    private final ImportReg registerTypeImport;
    private final WhiteList whiteList;

    private String groupId;
    private Rule rule;
    private String crn;

    public RegisterStep(GroupRepository grpRepo, PlanReg registerTypePlan, ImportReg registerTypeImport, WhiteList whiteList) {
        this.grpRepo = grpRepo;
        this.registerTypePlan = registerTypePlan;
        this.registerTypeImport = registerTypeImport;
        this.whiteList = whiteList;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    /**
     * Using this function we find registration type of the group.
     *
     * @return This function returns user entry point which is an integer.
     * user entry point=1 means that group has a plan.
     * user entry point=0 means that we should upload a file in order to register in the group.
     */
    private int findRegType() {
        return grpRepo.findByRuleId(rule.getRuleId()).getUserEntryPoint();
    }

    /**
     * @param user This function registers a user to the group.
     * @return After registration an activation will be created.
     */
    public Activation register(User user) throws ImportException, RegisterException, InterruptedException, IOException, ParseException {
        int entryPoint = findRegType();
        Activation activation;
        this.groupId = grpRepo.findByRuleId(rule.getRuleId()).getGroupId();
        switch (entryPoint) {
            case 0:
                log.info("Start registering by import.");
                registerTypeImport.setRule(rule);
                registerTypeImport.setGroupId(groupId);
                registerTypeImport.setCrn(crn);
                registerTypeImport.setUser(user);
                activation = registerTypeImport.registerUser();
                return activation;

            case 1:
                log.info("Start registering by plan.");
                registerTypePlan.setUser(user);
                registerTypePlan.setGroupId(groupId);
                activation = registerTypePlan.registerUser();
                return activation;
            case 4:
                log.info("Start registering by plan after adding user to whitelist.");
                whiteList.addToWhitelist(groupId, user.getCellNumber());
                registerTypePlan.setUser(user);
                registerTypePlan.setGroupId(groupId);
                activation = registerTypePlan.registerUser();
                return activation;
        }
        return null;

    }

    public void setTestData(Map<String, Object> testData) {
        this.crn = (String) testData.get("crn");
    }
}
