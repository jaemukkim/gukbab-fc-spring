package com.gukbabfc.notice.exception;

import com.gukbabfc.global.exception.ResourceNotFoundException;

public class NoticeNotFoundException extends ResourceNotFoundException {

    public NoticeNotFoundException() {
        super("공지사항을 찾을 수 없습니다.");
    }
}
