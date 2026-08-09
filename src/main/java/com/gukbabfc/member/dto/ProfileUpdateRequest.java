package com.gukbabfc.member.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 프로필 수정 화면에서 전달받은 입력값을 담습니다.
 */
public class ProfileUpdateRequest {

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(max = 30, message = "이름은 30자 이하로 입력해 주세요.")
    private String name;

    @Size(max = 10, message = "포지션은 10자 이하로 입력해 주세요.")
    private String position;

    @Min(value = 0, message = "등번호는 0 이상이어야 합니다.")
    @Max(value = 99, message = "등번호는 99 이하여야 합니다.")
    private Integer backNumber;

    @Size(max = 300, message = "소개는 300자 이하로 입력해 주세요.")
    private String introduction;

    public ProfileUpdateRequest() {
    }

    public ProfileUpdateRequest(String name, String position, Integer backNumber, String introduction) {
        this.name = name;
        this.position = position;
        this.backNumber = backNumber;
        this.introduction = introduction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getBackNumber() {
        return backNumber;
    }

    public void setBackNumber(Integer backNumber) {
        this.backNumber = backNumber;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
