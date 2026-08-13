package com.gukbabfc.uniform.service;

import com.gukbabfc.member.dao.MemberRepository;
import com.gukbabfc.member.entity.Member;
import com.gukbabfc.member.exception.MemberNotFoundException;
import com.gukbabfc.uniform.dao.UniformApplicationRepository;
import com.gukbabfc.uniform.dao.UniformOrderPeriodRepository;
import com.gukbabfc.uniform.dto.UniformApplicationRequest;
import com.gukbabfc.uniform.dto.UniformApplicationSummary;
import com.gukbabfc.uniform.dto.UniformApplicationView;
import com.gukbabfc.uniform.entity.UniformApplication;
import com.gukbabfc.uniform.entity.UniformOrderPeriod;
import com.gukbabfc.uniform.entity.UniformSize;
import com.gukbabfc.uniform.exception.UniformApplicationException;
import com.gukbabfc.uniform.exception.UniformOrderPeriodNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;

/**
 * 회원의 유니폼 신청 등록·수정·취소와 관리자 집계를 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class UniformApplicationService {

    private final UniformApplicationRepository applicationRepository;
    private final UniformOrderPeriodRepository periodRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Optional<UniformApplicationView> getMyApplication(Long periodId, String username) {
        UniformOrderPeriod period = findPeriod(periodId);
        Member member = findMember(username);
        return applicationRepository.findByPeriodIdAndMemberId(period.getId(), member.getId())
                .map(UniformApplicationView::from);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public UniformApplicationSummary getSummary(Long periodId) {
        findPeriod(periodId);
        var applications = applicationRepository.findAllWithMemberByPeriodId(periodId).stream()
                .map(UniformApplicationView::from)
                .toList();
        var quantityBySize = new EnumMap<UniformSize, Integer>(UniformSize.class);
        Arrays.stream(UniformSize.values()).forEach(size -> quantityBySize.put(size, 0));
        applications.forEach(application -> quantityBySize.merge(
                application.size(), application.quantity(), Integer::sum
        ));
        int totalQuantity = applications.stream()
                .mapToInt(UniformApplicationView::quantity)
                .sum();
        return new UniformApplicationSummary(applications, quantityBySize, totalQuantity);
    }

    @Transactional
    public void apply(Long periodId, String username, UniformApplicationRequest request) {
        UniformOrderPeriod period = findOpenPeriod(periodId);
        Member member = findMember(username);
        String markingName = request.getMarkingName().trim();

        applicationRepository.findByPeriodIdAndMemberId(periodId, member.getId())
                .ifPresentOrElse(
                        application -> application.update(
                                request.getSize(),
                                request.getBackNumber(),
                                markingName,
                                request.getQuantity()
                        ),
                        () -> applicationRepository.save(new UniformApplication(
                                period,
                                member,
                                request.getSize(),
                                request.getBackNumber(),
                                markingName,
                                request.getQuantity()
                        ))
                );
    }

    @Transactional
    public void cancel(Long periodId, String username) {
        UniformOrderPeriod period = findOpenPeriod(periodId);
        Member member = findMember(username);
        UniformApplication application = applicationRepository
                .findByPeriodIdAndMemberId(period.getId(), member.getId())
                .orElseThrow(() -> new UniformApplicationException("취소할 유니폼 신청이 없습니다."));
        applicationRepository.delete(application);
    }

    private UniformOrderPeriod findOpenPeriod(Long periodId) {
        UniformOrderPeriod period = findPeriod(periodId);
        if (!period.isOpenAt(LocalDateTime.now())) {
            throw new UniformApplicationException("현재 유니폼 신청 기간이 아닙니다.");
        }
        return period;
    }

    private UniformOrderPeriod findPeriod(Long periodId) {
        return periodRepository.findById(periodId)
                .orElseThrow(UniformOrderPeriodNotFoundException::new);
    }

    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }
}
