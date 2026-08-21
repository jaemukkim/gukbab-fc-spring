package com.gukbabfc.home.dto;

import com.gukbabfc.uniform.entity.UniformOrderPeriod;

import java.time.LocalDateTime;

/**
 * 대시보드에 표시할 현재 신청 가능한 유니폼 주문 기간입니다.
 */
public record OpenUniformOrderCard(
        Long id,
        String title,
        LocalDateTime endsAt
) {
    public static OpenUniformOrderCard from(UniformOrderPeriod period) {
        return new OpenUniformOrderCard(period.getId(), period.getTitle(), period.getEndsAt());
    }
}
