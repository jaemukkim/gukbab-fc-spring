package com.gukbabfc.uniform.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 구매 신청에서 선택할 수 있는 유니폼 사이즈입니다.
 */
@Getter
@RequiredArgsConstructor
public enum UniformSize {
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    TWO_XL("2XL");

    private final String label;
}
