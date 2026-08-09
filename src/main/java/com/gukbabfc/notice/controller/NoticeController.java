package com.gukbabfc.notice.controller;

import com.gukbabfc.notice.dto.NoticeCreateRequest;
import com.gukbabfc.notice.dto.NoticeDetail;
import com.gukbabfc.notice.dto.NoticeUpdateRequest;
import com.gukbabfc.notice.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

/**
 * 공지사항 목록과 관리자 CRUD 화면 요청을 처리합니다.
 */
@Controller
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("notices", noticeService.getNotices());
        return "notice/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.getNotice(id));
        return "notice/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("noticeCreateRequest", new NoticeCreateRequest());
        return "notice/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute NoticeCreateRequest noticeCreateRequest,
                         BindingResult bindingResult,
                         Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "notice/form";
        }
        Long noticeId = noticeService.createNotice(authentication.getName(), noticeCreateRequest);
        return "redirect:/notices/" + noticeId;
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {
        NoticeDetail notice = noticeService.getNotice(id);
        model.addAttribute("noticeId", id);
        model.addAttribute("noticeUpdateRequest", NoticeUpdateRequest.from(notice));
        return "notice/edit-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute NoticeUpdateRequest noticeUpdateRequest,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("noticeId", id);
            return "notice/edit-form";
        }
        noticeService.updateNotice(id, noticeUpdateRequest);
        return "redirect:/notices/" + id + "?updated";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/notices?deleted";
    }
}
