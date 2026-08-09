package com.gukbabfc.member.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

public class MemberNotFoundException extends ResourceNotFoundException {

    public MemberNotFoundException() {
        super("회원을 찾을 수 없습니다.");
    }
}
