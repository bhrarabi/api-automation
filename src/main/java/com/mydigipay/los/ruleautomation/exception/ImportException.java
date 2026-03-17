package com.mydigipay.los.ruleautomation.exception;

/**
 * Possible exceptions to registration by uploading a file.
 */
public class ImportException extends Exception {
    public ImportException(String errorMessage) {
        super(errorMessage);
    }

}
