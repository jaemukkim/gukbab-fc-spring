package com.gukbabfc.member.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

/**
 * 조회한 회원이 존재하지 않을 때 발생합니다.
 */
public class MemberNotFoundException extends ResourceNotFoundException {

    public MemberNotFoundException() {
        super("회원을 찾을 수 없습니다.");
    }
}
