package com.mydigipay.los.ruleautomation.model.steps.register;

import com.mydigipay.los.ruleautomation.exception.ImportException;
import com.mydigipay.los.ruleautomation.exception.RegisterException;
import com.mydigipay.los.ruleautomation.model.activation.Activation;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;

/**
 * This is an interface to implement different types of registration: using plan, upload file or whiteList
 */
@Component
public interface RegisterType {

    Activation registerUser() throws ImportException, RegisterException, InterruptedException, IOException, ParseException;

}
