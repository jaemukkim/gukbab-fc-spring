package com.gukbabfc.uniform.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 현재 시각과 수동 마감 여부를 반영한 유니폼 신청 기간 상태입니다.
 */
@Getter
@RequiredArgsConstructor
public enum UniformOrderStatus {
    UPCOMING("신청 전"),
    OPEN("신청 중"),
    CLOSED("마감");

    private final String label;
}
