package com.data.organization.exception;

public class FormDataException extends RuntimeException {
    private String message;

    public FormDataException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
