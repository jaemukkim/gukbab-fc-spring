package com.gukbabfc.uniform.dto;

import com.gukbabfc.uniform.entity.UniformApplication;
import com.gukbabfc.uniform.entity.UniformSize;

import java.time.LocalDateTime;

/**
 * 본인 신청 정보와 관리자 신청자 목록에 표시할 데이터를 전달합니다.
 */
public record UniformApplicationView(
        Long id,
        String username,
        String memberName,
        UniformSize size,
        Integer backNumber,
        String markingName,
        Integer quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UniformApplicationView from(UniformApplication application) {
        return new UniformApplicationView(
                application.getId(),
                application.getMember().getUsername(),
                application.getMember().getName(),
                application.getSize(),
                application.getBackNumber(),
                application.getMarkingName(),
                application.getQuantity(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}
