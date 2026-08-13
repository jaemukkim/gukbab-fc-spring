package com.gukbabfc.uniform.dto;

import com.gukbabfc.uniform.entity.UniformSize;

import java.util.List;
import java.util.Map;

/**
 * 관리자가 확인할 전체 신청 목록과 사이즈별 수량 합계를 전달합니다.
 */
public record UniformApplicationSummary(
        List<UniformApplicationView> applications,
        Map<UniformSize, Integer> quantityBySize,
        int totalQuantity
) {
}
