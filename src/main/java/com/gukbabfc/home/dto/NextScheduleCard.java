package com.gukbabfc.home.dto;

import com.gukbabfc.schedule.entity.ParticipationStatus;
import com.gukbabfc.schedule.entity.Schedule;

import java.time.LocalDateTime;

/**
 * 대시보드에 표시할 가장 가까운 풋살 일정과 로그인 회원의 참가 상태입니다.
 */
public record NextScheduleCard(
        Long id,
        String title,
        String location,
        LocalDateTime scheduledAt,
        ParticipationStatus participationStatus
) {
    public static NextScheduleCard from(Schedule schedule, ParticipationStatus participationStatus) {
        return new NextScheduleCard(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getLocation(),
                schedule.getScheduledAt(),
                participationStatus
        );
    }
}
