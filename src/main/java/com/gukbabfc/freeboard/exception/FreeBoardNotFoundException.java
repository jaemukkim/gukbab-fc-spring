package com.gukbabfc.freeboard.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

/**
 * 요청한 자유게시글이 존재하지 않을 때 발생합니다.
 */
public class FreeBoardNotFoundException extends ResourceNotFoundException {

    public FreeBoardNotFoundException() {
        super("자유게시판 글을 찾을 수 없습니다.");
    }
}
