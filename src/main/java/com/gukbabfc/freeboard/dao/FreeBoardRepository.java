package com.gukbabfc.freeboard.dao;

import com.gukbabfc.freeboard.entity.FreeBoardPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 자유게시글의 저장, 페이징, 검색을 담당하는 JPA 저장소입니다.
 */
public interface FreeBoardRepository extends JpaRepository<FreeBoardPost, Long> {

    List<FreeBoardPost> findTop3ByOrderByCreatedAtDesc();

    Page<FreeBoardPost> findByTitleContainingOrContentContaining(
            String titleKeyword,
            String contentKeyword,
            Pageable pageable
    );
}
