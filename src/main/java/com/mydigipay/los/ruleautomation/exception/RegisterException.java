package com.mydigipay.los.ruleautomation.exception;

/**
 * Possible exceptions to registration by plan.
 */
public class RegisterException extends Exception {
    public RegisterException(String errorMessage) {
        super(errorMessage);
    }
}
