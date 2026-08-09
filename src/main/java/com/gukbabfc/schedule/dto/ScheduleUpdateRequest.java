package com.gukbabfc.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 풋살 일정 수정 입력값과 검증 규칙을 담습니다.
 */
@Getter
@Setter
public class ScheduleUpdateRequest {

    @NotBlank(message = "일정 제목을 입력해 주세요.")
    @Size(max = 100, message = "일정 제목은 100자 이하로 입력해 주세요.")
    private String title;

    @NotBlank(message = "장소를 입력해 주세요.")
    @Size(max = 100, message = "장소는 100자 이하로 입력해 주세요.")
    private String location;

    @NotNull(message = "경기 일시를 입력해 주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime scheduledAt;

    @Size(max = 3000, message = "상세 안내는 3,000자 이하로 입력해 주세요.")
    private String description;

    public static ScheduleUpdateRequest from(ScheduleDetail detail) {
        ScheduleUpdateRequest request = new ScheduleUpdateRequest();
        request.setTitle(detail.title());
        request.setLocation(detail.location());
        request.setScheduledAt(detail.scheduledAt());
        request.setDescription(detail.description());
        return request;
    }
}
