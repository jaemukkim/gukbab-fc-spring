package com.gukbabfc.uniform.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

/**
 * 요청한 유니폼 신청 기간이 존재하지 않을 때 발생합니다.
 */
public class UniformOrderPeriodNotFoundException extends ResourceNotFoundException {

    public UniformOrderPeriodNotFoundException() {
        super("유니폼 신청 기간을 찾을 수 없습니다.");
    }
}
