package com.gukbabfc.freeboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 자유게시판 댓글 등록·수정 입력값과 검증 규칙을 담습니다.
 */
@Getter
@Setter
public class FreeBoardCommentRequest {

    @NotBlank(message = "댓글 내용을 입력해 주세요.")
    @Size(max = 1000, message = "댓글은 1,000자 이하로 입력해 주세요.")
    private String content;
}
