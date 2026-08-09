package com.gukbabfc.freeboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * 자유게시글 등록 입력값과 검증 규칙을 담습니다.
 */
public class FreeBoardCreateRequest {

    @NotBlank(message = "제목을 입력해 주세요.")
    @Size(max = 200, message = "제목은 200자 이하로 입력해 주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해 주세요.")
    @Size(max = 10000, message = "내용은 10,000자 이하로 입력해 주세요.")
    private String content;
}
