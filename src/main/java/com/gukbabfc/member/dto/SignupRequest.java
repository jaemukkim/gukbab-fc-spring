package com.gukbabfc.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 화면의 입력값과 검증 규칙을 담습니다.
 */
public class SignupRequest {

    @NotBlank(message = "아이디를 입력해 주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자로 입력해 주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문과 숫자만 사용할 수 있습니다.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 64, message = "비밀번호는 8~64자로 입력해 주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private String passwordConfirm;

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(max = 30, message = "이름은 30자 이하로 입력해 주세요.")
    private String name;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
