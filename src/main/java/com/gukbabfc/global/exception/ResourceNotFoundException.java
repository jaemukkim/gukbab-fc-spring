package com.gukbabfc.global.exception;

/**
 * 요청한 리소스를 찾지 못했을 때 사용하는 공통 예외입니다.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
