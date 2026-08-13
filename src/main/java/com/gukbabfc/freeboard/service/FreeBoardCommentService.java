package com.gukbabfc.freeboard.service;

import com.gukbabfc.freeboard.dao.FreeBoardCommentRepository;
import com.gukbabfc.freeboard.dao.FreeBoardRepository;
import com.gukbabfc.freeboard.dto.FreeBoardCommentRequest;
import com.gukbabfc.freeboard.dto.FreeBoardCommentView;
import com.gukbabfc.freeboard.entity.FreeBoardComment;
import com.gukbabfc.freeboard.entity.FreeBoardPost;
import com.gukbabfc.freeboard.exception.FreeBoardCommentNotFoundException;
import com.gukbabfc.freeboard.exception.FreeBoardNotFoundException;
import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.member.entity.MemberRole;
import com.gukbabfc.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글 조회·작성과 작성자·관리자 수정 및 삭제 권한을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class FreeBoardCommentService {

    private final FreeBoardCommentRepository commentRepository;
    private final FreeBoardRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<FreeBoardCommentView> getComments(Long postId, String username) {
        findPost(postId);
        Member member = findMember(username);
        return commentRepository.findAllWithAuthorByPostId(postId).stream()
                .map(comment -> FreeBoardCommentView.from(comment, isManager(comment, member)))
                .toList();
    }

    @Transactional
    public void createComment(Long postId, String username, FreeBoardCommentRequest request) {
        FreeBoardPost post = findPost(postId);
        Member author = findMember(username);
        commentRepository.save(new FreeBoardComment(post, author, request.getContent().trim()));
    }

    @Transactional
    public void updateComment(Long postId, Long commentId, String username,
                              FreeBoardCommentRequest request) {
        FreeBoardComment comment = findComment(postId, commentId);
        validateManager(comment, findMember(username));
        comment.update(request.getContent().trim());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, String username) {
        FreeBoardComment comment = findComment(postId, commentId);
        validateManager(comment, findMember(username));
        commentRepository.delete(comment);
    }

    private FreeBoardPost findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(FreeBoardNotFoundException::new);
    }

    private FreeBoardComment findComment(Long postId, Long commentId) {
        return commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(FreeBoardCommentNotFoundException::new);
    }

    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void validateManager(FreeBoardComment comment, Member member) {
        if (!isManager(comment, member)) {
            throw new AccessDeniedException("댓글을 수정하거나 삭제할 권한이 없습니다.");
        }
    }

    private boolean isManager(FreeBoardComment comment, Member member) {
        return member.getRole() == MemberRole.ADMIN
                || comment.getAuthor().getId().equals(member.getId());
    }
}
