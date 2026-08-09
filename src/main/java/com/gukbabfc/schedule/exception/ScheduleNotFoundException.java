package com.gukbabfc.schedule.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

public class ScheduleNotFoundException extends ResourceNotFoundException {

    public ScheduleNotFoundException() {
        super("풋살 일정을 찾을 수 없습니다.");
    }
}
