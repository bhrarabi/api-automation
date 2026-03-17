package com.mydigipay.los.ruleautomation.model.steps.register;

import com.mydigipay.los.ruleautomation.exception.ImportException;
import com.mydigipay.los.ruleautomation.model.activation.Activation;
import com.mydigipay.los.ruleautomation.model.rule.Rule;
import com.mydigipay.los.ruleautomation.model.user.User;
import com.mydigipay.los.ruleautomation.service.stepservices.register.RegisterService;
import com.mydigipay.los.ruleautomation.service.activationservice.FetchActivation;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Data
public class ImportReg implements RegisterType {

    final
    RegisterService regService;
    final
    FetchActivation fetch;
    private Rule rule;
private String crn;
private User user;
private String groupId;
    public ImportReg(RegisterService regService, FetchActivation fetch) {

        this.regService = regService;
        this.fetch = fetch;
    }

    @Override
    public Activation registerUser() throws ImportException, InterruptedException, IOException {
      String type=rule.getCustomerType();
        String creditId;
      if(type.equals("ORGANIZATIONAL")){
          creditId= regService.orgRegisterByImport(groupId,rule,user, crn);
      }
      else{
         creditId = regService.individualRegisterByImport(groupId,rule,user);

    }
        return fetch.fetchByCreditId(creditId);

}}
