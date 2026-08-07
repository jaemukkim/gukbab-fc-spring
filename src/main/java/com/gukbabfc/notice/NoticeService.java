package com.gukbabfc.notice;

import com.gukbabfc.member.Member;
import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.notice.dao.NoticeRepository;
import com.gukbabfc.notice.dto.NoticeCreateRequest;
import com.gukbabfc.notice.dto.NoticeDetail;
import com.gukbabfc.notice.dto.NoticeListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<NoticeListItem> getNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(NoticeListItem::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NoticeDetail getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다."));
        return NoticeDetail.from(notice);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Long createNotice(String username, NoticeCreateRequest request) {
        Member author = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Notice notice = new Notice(request.getTitle().trim(), request.getContent().trim(), author);
        return noticeRepository.save(notice).getId();
    }
}
