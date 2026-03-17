package com.mydigipay.los.ruleautomation.model.steps.profile;

import com.mydigipay.los.ruleautomation.model.activation.pojo.StepCode;
import com.mydigipay.los.ruleautomation.model.address.Address;
import com.mydigipay.los.ruleautomation.model.steps.Step;
import com.mydigipay.los.ruleautomation.model.steps.profile.pojo.ProfileStatus;
import com.mydigipay.los.ruleautomation.service.stepservices.StepService;
import com.mydigipay.los.ruleautomation.service.stepservices.profile.ProfileService;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Data
public class ProfileStep extends Step {
    final
    ProfileService profileService;

    private Address address;

    public ProfileStep(ProfileService profileService, StepService service) {
        super.setStepService(service);
        setCode(StepCode.PROFILE);
        this.profileService = profileService;
    }

    @Override
    public void preconditionPreparation() {

        setPreviousStepCompleteStatus();

        profileService.startProfileStep(getUser(), getActivation());
    }

    @Override
    public Boolean checkPostConditions() {

        return checkStatusInJournal(ProfileStatus.PROFILE_COMPLETED.name());


    }

    @Override
    public void process() throws Exception {


        if (getProcessType().equals("USER_PROCESS")) {
            preconditionPreparation();
            if (getStatus().equals(ProfileStatus.PROFILE_IN_PROGRESS.name())) {

                profileService.updateProfile(getUser(), getActivation(), getAddress());
            }

        } else {
            List<String> finalStatuses = new ArrayList<>();
            finalStatuses.add(ProfileStatus.PROFILE_COMPLETED.name());
            finalStatuses.add(ProfileStatus.PROFILE_REJECTED.name());
            setPreviousStepCompleteStatus();
            autoProcess(getActivation(), finalStatuses);
        }


    }

    @Override
    public String getCompleteStatus() {
        return "PROFILE_COMPLETED";
    }

    @Override
    public void setTestData(Map<String, Object> testData) {
        setAddress((Address) testData.get("Address"));
    }


}

