package com.gukbabfc.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
/**
 * 공지사항 수정 입력값과 검증 규칙을 담습니다.
 */
public class NoticeUpdateRequest {

    @NotBlank(message = "제목을 입력해 주세요.")
    @Size(max = 200, message = "제목은 200자 이하로 입력해 주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해 주세요.")
    @Size(max = 10000, message = "내용은 10,000자 이하로 입력해 주세요.")
    private String content;

    public NoticeUpdateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static NoticeUpdateRequest from(NoticeDetail notice) {
        return new NoticeUpdateRequest(notice.title(), notice.content());
    }
}
