package com.gukbabfc.member.exception;

public class SignupException extends RuntimeException {

    private final String field;

    public SignupException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
