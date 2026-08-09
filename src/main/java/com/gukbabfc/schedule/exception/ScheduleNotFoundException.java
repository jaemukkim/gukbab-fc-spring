package com.gukbabfc.schedule.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

/**
 * 요청한 풋살 일정이 존재하지 않을 때 발생합니다.
 */
public class ScheduleNotFoundException extends ResourceNotFoundException {

    public ScheduleNotFoundException() {
        super("풋살 일정을 찾을 수 없습니다.");
    }
}
