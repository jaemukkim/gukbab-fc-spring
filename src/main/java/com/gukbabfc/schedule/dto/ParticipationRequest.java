package com.gukbabfc.schedule.dto;

import com.gukbabfc.schedule.entity.ParticipationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 일정 상세 화면에서 선택한 참가 상태를 전달받습니다.
 */
@Getter
@Setter
public class ParticipationRequest {

    @NotNull(message = "참가 상태를 선택해 주세요.")
    private ParticipationStatus status;
}
