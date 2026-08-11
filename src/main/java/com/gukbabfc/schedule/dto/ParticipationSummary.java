package com.gukbabfc.schedule.dto;

import com.gukbabfc.schedule.entity.ParticipationStatus;

import java.util.List;

/**
 * 내 응답 상태와 상태별 회원 목록을 일정 상세 화면에 전달합니다.
 */
public record ParticipationSummary(
        ParticipationStatus myStatus,
        List<ParticipantItem> attendingMembers,
        List<ParticipantItem> notAttendingMembers,
        List<ParticipantItem> undecidedMembers
) {
    public int attendingCount() {
        return attendingMembers.size();
    }

    public int notAttendingCount() {
        return notAttendingMembers.size();
    }

    public int undecidedCount() {
        return undecidedMembers.size();
    }
}
