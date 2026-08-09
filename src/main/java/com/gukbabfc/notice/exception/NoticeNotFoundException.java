package com.gukbabfc.notice.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

/**
 * 요청한 공지사항이 존재하지 않을 때 발생합니다.
 */
public class NoticeNotFoundException extends ResourceNotFoundException {

    public NoticeNotFoundException() {
        super("공지사항을 찾을 수 없습니다.");
    }
}
