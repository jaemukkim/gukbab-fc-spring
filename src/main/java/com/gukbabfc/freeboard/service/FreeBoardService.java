package com.gukbabfc.freeboard.service;

import com.gukbabfc.freeboard.dao.FreeBoardRepository;
import com.gukbabfc.freeboard.dto.FreeBoardCreateRequest;
import com.gukbabfc.freeboard.dto.FreeBoardDetail;
import com.gukbabfc.freeboard.dto.FreeBoardListItem;
import com.gukbabfc.freeboard.dto.FreeBoardUpdateRequest;
import com.gukbabfc.freeboard.entity.FreeBoardPost;
import com.gukbabfc.freeboard.exception.FreeBoardNotFoundException;
import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.member.entity.MemberRole;
import com.gukbabfc.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 자유게시글 CRUD, 검색, 작성자 권한 업무 규칙을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class FreeBoardService {

    private static final int PAGE_SIZE = 10;

    private final FreeBoardRepository freeBoardRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<FreeBoardListItem> getPosts(int page, String keyword) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                PAGE_SIZE,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        );
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        Page<FreeBoardPost> posts = normalizedKeyword.isBlank()
                ? freeBoardRepository.findAll(pageable)
                : freeBoardRepository.findByTitleContainingOrContentContaining(
                        normalizedKeyword,
                        normalizedKeyword,
                        pageable
                );

        return posts.map(FreeBoardListItem::from);
    }

    @Transactional(readOnly = true)
    public FreeBoardDetail getPost(Long id) {
        return FreeBoardDetail.from(findPost(id));
    }

    @Transactional
    public Long createPost(String username, FreeBoardCreateRequest request) {
        Member author = findMember(username);
        FreeBoardPost post = new FreeBoardPost(
                request.getTitle().trim(),
                request.getContent().trim(),
                author
        );
        return freeBoardRepository.save(post).getId();
    }

    @Transactional(readOnly = true)
    public FreeBoardUpdateRequest getUpdateRequest(Long id, String username) {
        FreeBoardPost post = findPost(id);
        validateManager(post, username);
        return FreeBoardUpdateRequest.from(FreeBoardDetail.from(post));
    }

    @Transactional
    public void updatePost(Long id, String username, FreeBoardUpdateRequest request) {
        FreeBoardPost post = findPost(id);
        validateManager(post, username);
        post.update(request.getTitle().trim(), request.getContent().trim());
    }

    @Transactional
    public void deletePost(Long id, String username) {
        FreeBoardPost post = findPost(id);
        validateManager(post, username);
        freeBoardRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public boolean canManage(Long id, String username) {
        FreeBoardPost post = findPost(id);
        Member member = findMember(username);
        return isManager(post, member);
    }

    private void validateManager(FreeBoardPost post, String username) {
        Member member = findMember(username);
        if (!isManager(post, member)) {
            throw new AccessDeniedException("게시글을 수정하거나 삭제할 권한이 없습니다.");
        }
    }

    private boolean isManager(FreeBoardPost post, Member member) {
        return member.getRole() == MemberRole.ADMIN
                || post.getAuthor().getId().equals(member.getId());
    }

    private FreeBoardPost findPost(Long id) {
        return freeBoardRepository.findById(id)
                .orElseThrow(FreeBoardNotFoundException::new);
    }

    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }
}
