package com.gukbabfc.uniform.service;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.member.exception.MemberNotFoundException;
import com.gukbabfc.uniform.dao.UniformOrderPeriodRepository;
import com.gukbabfc.uniform.dto.UniformOrderPeriodRequest;
import com.gukbabfc.uniform.dto.UniformOrderPeriodView;
import com.gukbabfc.uniform.entity.UniformOrderPeriod;
import com.gukbabfc.uniform.exception.UniformOrderPeriodNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 유니폼 신청 기간의 조회, 등록, 수정, 마감 업무 규칙을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class UniformOrderPeriodService {

    private final UniformOrderPeriodRepository periodRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<UniformOrderPeriodView> getPeriods() {
        LocalDateTime now = LocalDateTime.now();
        return periodRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(period -> UniformOrderPeriodView.from(period, now))
                .toList();
    }

    @Transactional(readOnly = true)
    public UniformOrderPeriodView getPeriod(Long id) {
        return UniformOrderPeriodView.from(findPeriod(id), LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public UniformOrderPeriodRequest getUpdateRequest(Long id) {
        return UniformOrderPeriodRequest.from(findPeriod(id));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Long createPeriod(String username, UniformOrderPeriodRequest request) {
        Member createdBy = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
        UniformOrderPeriod period = new UniformOrderPeriod(
                request.getTitle().trim(),
                normalize(request.getDescription()),
                request.getStartsAt(),
                request.getEndsAt(),
                createdBy
        );
        return periodRepository.save(period).getId();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updatePeriod(Long id, UniformOrderPeriodRequest request) {
        UniformOrderPeriod period = findPeriod(id);
        period.update(
                request.getTitle().trim(),
                normalize(request.getDescription()),
                request.getStartsAt(),
                request.getEndsAt()
        );
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void closePeriod(Long id) {
        findPeriod(id).close();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void reopenPeriod(Long id) {
        findPeriod(id).reopen();
    }

    private UniformOrderPeriod findPeriod(Long id) {
        return periodRepository.findById(id)
                .orElseThrow(UniformOrderPeriodNotFoundException::new);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
