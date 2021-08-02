package com.epam.esm.webservice.controller.handler;

public class ErrorMessage {

    private final String message;
    private final int errorCode;

    public ErrorMessage(String localizedMessage, int customErrorCode) {
        this.message = localizedMessage;
        this.errorCode = customErrorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
