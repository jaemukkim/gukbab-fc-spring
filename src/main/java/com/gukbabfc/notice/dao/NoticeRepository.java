package com.gukbabfc.notice.dao;

import com.gukbabfc.notice.entity.Notice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 공지사항의 저장과 최신순 조회를 담당하는 JPA 저장소입니다.
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByOrderByCreatedAtDesc();
}
