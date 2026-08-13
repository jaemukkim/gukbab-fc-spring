package com.gukbabfc.freeboard.dto;

/**
 * 게시글별 댓글 수 집계 쿼리 결과를 전달하는 인터페이스입니다.
 */
public interface FreeBoardCommentCount {

    Long getPostId();

    long getCommentCount();
}
