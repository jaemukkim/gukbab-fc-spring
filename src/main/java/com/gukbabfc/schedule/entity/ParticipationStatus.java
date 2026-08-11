package com.gukbabfc.schedule.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원이 풋살 일정에 응답할 수 있는 참가 상태입니다.
 */
@Getter
@RequiredArgsConstructor
public enum ParticipationStatus {
    ATTENDING("참가"),
    NOT_ATTENDING("불참"),
    UNDECIDED("미정");

    private final String label;
}
