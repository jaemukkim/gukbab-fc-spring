package com.gukbabfc.freeboard.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

/**
 * 요청한 댓글이 없거나 해당 게시글 소속이 아닐 때 발생합니다.
 */
public class FreeBoardCommentNotFoundException extends ResourceNotFoundException {

    public FreeBoardCommentNotFoundException() {
        super("자유게시판 댓글을 찾을 수 없습니다.");
    }
}
