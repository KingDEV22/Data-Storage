package com.data.organization.exception;

public class MetaDataException extends RuntimeException {
    private String message;

    public MetaDataException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
