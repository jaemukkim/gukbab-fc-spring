package com.gukbabfc.freeboard.dao;

import com.gukbabfc.freeboard.entity.FreeBoardPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeBoardRepository extends JpaRepository<FreeBoardPost, Long> {

    Page<FreeBoardPost> findByTitleContainingOrContentContaining(
            String titleKeyword,
            String contentKeyword,
            Pageable pageable
    );
}
