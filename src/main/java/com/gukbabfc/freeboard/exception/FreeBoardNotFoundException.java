package com.gukbabfc.freeboard.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

public class FreeBoardNotFoundException extends ResourceNotFoundException {

    public FreeBoardNotFoundException() {
        super("자유게시판 글을 찾을 수 없습니다.");
    }
}
