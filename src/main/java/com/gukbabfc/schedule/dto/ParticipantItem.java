package com.gukbabfc.schedule.dto;

import com.gukbabfc.schedule.entity.ScheduleParticipation;

import java.time.LocalDateTime;

/**
 * 참가 현황에 표시할 회원 한 명의 응답 정보를 전달합니다.
 */
public record ParticipantItem(
        String username,
        String name,
        LocalDateTime respondedAt
) {
    public static ParticipantItem from(ScheduleParticipation participation) {
        return new ParticipantItem(
                participation.getMember().getUsername(),
                participation.getMember().getName(),
                participation.getRespondedAt()
        );
    }
}
