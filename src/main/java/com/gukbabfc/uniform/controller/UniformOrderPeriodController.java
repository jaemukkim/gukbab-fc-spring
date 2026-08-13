package com.gukbabfc.uniform.controller;

import com.gukbabfc.uniform.dto.UniformOrderPeriodRequest;
import com.gukbabfc.uniform.service.UniformOrderPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 유니폼 신청 기간 목록과 관리자 등록·수정·마감 요청을 처리합니다.
 */
@Controller
@RequestMapping("/uniform-orders")
@RequiredArgsConstructor
public class UniformOrderPeriodController {

    private final UniformOrderPeriodService periodService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("periods", periodService.getPeriods());
        return "uniform/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("period", periodService.getPeriod(id));
        return "uniform/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("uniformOrderPeriodRequest", new UniformOrderPeriodRequest());
        return "uniform/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute UniformOrderPeriodRequest uniformOrderPeriodRequest,
                         BindingResult bindingResult,
                         Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "uniform/form";
        }
        Long id = periodService.createPeriod(authentication.getName(), uniformOrderPeriodRequest);
        return "redirect:/uniform-orders/" + id;
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {
        model.addAttribute("periodId", id);
        model.addAttribute("uniformOrderPeriodRequest", periodService.getUpdateRequest(id));
        return "uniform/edit-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute UniformOrderPeriodRequest uniformOrderPeriodRequest,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("periodId", id);
            return "uniform/edit-form";
        }
        periodService.updatePeriod(id, uniformOrderPeriodRequest);
        return "redirect:/uniform-orders/" + id + "?updated";
    }

    @PostMapping("/{id}/close")
    public String close(@PathVariable Long id) {
        periodService.closePeriod(id);
        return "redirect:/uniform-orders/" + id + "?closed";
    }

    @PostMapping("/{id}/reopen")
    public String reopen(@PathVariable Long id) {
        periodService.reopenPeriod(id);
        return "redirect:/uniform-orders/" + id + "?reopened";
    }
}
