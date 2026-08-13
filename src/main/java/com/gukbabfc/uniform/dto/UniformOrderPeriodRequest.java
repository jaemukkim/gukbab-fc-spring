package com.gukbabfc.uniform.dto;

import com.gukbabfc.uniform.entity.UniformOrderPeriod;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 유니폼 신청 기간 등록·수정 입력값과 검증 규칙을 담습니다.
 */
@Getter
@Setter
public class UniformOrderPeriodRequest {

    @NotBlank(message = "신청 제목을 입력해 주세요.")
    @Size(max = 100, message = "신청 제목은 100자 이하로 입력해 주세요.")
    private String title;

    @Size(max = 3000, message = "안내 내용은 3,000자 이하로 입력해 주세요.")
    private String description;

    @NotNull(message = "신청 시작 시각을 입력해 주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startsAt;

    @NotNull(message = "신청 마감 시각을 입력해 주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endsAt;

    @AssertTrue(message = "신청 마감 시각은 시작 시각보다 늦어야 합니다.")
    public boolean isPeriodValid() {
        return startsAt == null || endsAt == null || endsAt.isAfter(startsAt);
    }

    public static UniformOrderPeriodRequest from(UniformOrderPeriod period) {
        UniformOrderPeriodRequest request = new UniformOrderPeriodRequest();
        request.setTitle(period.getTitle());
        request.setDescription(period.getDescription());
        request.setStartsAt(period.getStartsAt());
        request.setEndsAt(period.getEndsAt());
        return request;
    }
}
