package com.gukbabfc.freeboard.dao;

import com.gukbabfc.freeboard.entity.FreeBoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreeBoardRepository extends JpaRepository<FreeBoardPost, Long> {

    List<FreeBoardPost> findAllByOrderByCreatedAtDesc();
}
