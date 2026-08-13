package com.gukbabfc.freeboard.dao;

import com.gukbabfc.freeboard.entity.FreeBoardComment;
import com.gukbabfc.freeboard.dto.FreeBoardCommentCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 자유게시판 댓글의 저장, 게시글별 조회, 일괄 삭제를 담당합니다.
 */
public interface FreeBoardCommentRepository extends JpaRepository<FreeBoardComment, Long> {

    @Query("""
            select comment
            from FreeBoardComment comment
            join fetch comment.author
            where comment.post.id = :postId
            order by comment.createdAt asc, comment.id asc
            """)
    List<FreeBoardComment> findAllWithAuthorByPostId(@Param("postId") Long postId);

    @Query("""
            select comment.post.id as postId, count(comment.id) as commentCount
            from FreeBoardComment comment
            where comment.post.id in :postIds
            group by comment.post.id
            """)
    List<FreeBoardCommentCount> countByPostIds(@Param("postIds") List<Long> postIds);

    Optional<FreeBoardComment> findByIdAndPostId(Long id, Long postId);

    @Modifying
    @Query("delete from FreeBoardComment comment where comment.post.id = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);
}
