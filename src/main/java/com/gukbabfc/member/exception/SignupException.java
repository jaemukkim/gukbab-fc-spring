package com.gukbabfc.member.exception;

/**
 * 회원가입 입력값이나 중복 계정 검증 실패를 표현합니다.
 */
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
