package com.gukbabfc.uniform.dto;

import com.gukbabfc.uniform.entity.UniformOrderPeriod;
import com.gukbabfc.uniform.entity.UniformOrderStatus;

import java.time.LocalDateTime;

/**
 * 유니폼 신청 기간 목록과 상세 화면에 전달할 정보를 담습니다.
 */
public record UniformOrderPeriodView(
        Long id,
        String title,
        String description,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        UniformOrderStatus status,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UniformOrderPeriodView from(UniformOrderPeriod period, LocalDateTime now) {
        return new UniformOrderPeriodView(
                period.getId(),
                period.getTitle(),
                period.getDescription(),
                period.getStartsAt(),
                period.getEndsAt(),
                resolveStatus(period, now),
                period.getCreatedBy().getName(),
                period.getCreatedAt(),
                period.getUpdatedAt()
        );
    }

    private static UniformOrderStatus resolveStatus(UniformOrderPeriod period, LocalDateTime now) {
        if (period.isClosed() || !now.isBefore(period.getEndsAt())) {
            return UniformOrderStatus.CLOSED;
        }
        if (now.isBefore(period.getStartsAt())) {
            return UniformOrderStatus.UPCOMING;
        }
        return UniformOrderStatus.OPEN;
    }
}
