package com.gukbabfc.uniform.exception;

/**
 * 신청 기간이 아니거나 취소할 신청이 없을 때 발생하는 업무 예외입니다.
 */
public class UniformApplicationException extends RuntimeException {

    public UniformApplicationException(String message) {
        super(message);
    }
}
