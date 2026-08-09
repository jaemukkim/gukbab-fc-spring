package com.gukbabfc.schedule.dto;

import com.gukbabfc.schedule.entity.Schedule;

import java.time.LocalDateTime;

/**
 * 풋살 일정 상세 화면에 전달할 정보를 담습니다.
 */
public record ScheduleDetail(
        Long id,
        String title,
        String location,
        LocalDateTime scheduledAt,
        String description,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ScheduleDetail from(Schedule schedule) {
        return new ScheduleDetail(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getLocation(),
                schedule.getScheduledAt(),
                schedule.getDescription(),
                schedule.getCreatedBy().getName(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }
}
