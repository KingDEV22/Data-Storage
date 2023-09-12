package com.data.organization.exception;

public class RecordDataException extends RuntimeException {
    private String message;

    public RecordDataException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
