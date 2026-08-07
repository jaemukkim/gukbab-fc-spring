package com.gukbabfc.notice;

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

@Controller
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

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
}
