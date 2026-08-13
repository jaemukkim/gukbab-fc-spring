package com.gukbabfc.uniform.controller;

import com.gukbabfc.uniform.dto.UniformOrderPeriodRequest;
import com.gukbabfc.uniform.dto.UniformApplicationRequest;
import com.gukbabfc.uniform.dto.UniformApplicationView;
import com.gukbabfc.uniform.entity.UniformSize;
import com.gukbabfc.uniform.exception.UniformApplicationException;
import com.gukbabfc.uniform.service.UniformApplicationService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * 유니폼 신청 기간 목록과 관리자 등록·수정·마감 요청을 처리합니다.
 */
@Controller
@RequestMapping("/uniform-orders")
@RequiredArgsConstructor
public class UniformOrderPeriodController {

    private final UniformOrderPeriodService periodService;
    private final UniformApplicationService applicationService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("periods", periodService.getPeriods());
        return "uniform/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication authentication, Model model) {
        Optional<UniformApplicationView> myApplication = applicationService
                .getMyApplication(id, authentication.getName());
        UniformApplicationRequest request = myApplication
                .map(UniformApplicationRequest::from)
                .orElseGet(UniformApplicationRequest::new);
        populateDetailModel(id, authentication, model, myApplication, request);
        return "uniform/detail";
    }

    @PostMapping("/{id}/application")
    public String apply(@PathVariable Long id,
                        @Valid @ModelAttribute UniformApplicationRequest uniformApplicationRequest,
                        BindingResult bindingResult,
                        Authentication authentication,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Optional<UniformApplicationView> myApplication = applicationService
                    .getMyApplication(id, authentication.getName());
            populateDetailModel(
                    id, authentication, model, myApplication, uniformApplicationRequest
            );
            return "uniform/detail";
        }
        try {
            applicationService.apply(id, authentication.getName(), uniformApplicationRequest);
            return "redirect:/uniform-orders/" + id + "?applicationSaved";
        } catch (UniformApplicationException exception) {
            redirectAttributes.addFlashAttribute("applicationError", exception.getMessage());
            return "redirect:/uniform-orders/" + id;
        }
    }

    @PostMapping("/{id}/application/cancel")
    public String cancelApplication(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            applicationService.cancel(id, authentication.getName());
            return "redirect:/uniform-orders/" + id + "?applicationCancelled";
        } catch (UniformApplicationException exception) {
            redirectAttributes.addFlashAttribute("applicationError", exception.getMessage());
            return "redirect:/uniform-orders/" + id;
        }
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

    private void populateDetailModel(Long id,
                                     Authentication authentication,
                                     Model model,
                                     Optional<UniformApplicationView> myApplication,
                                     UniformApplicationRequest request) {
        model.addAttribute("period", periodService.getPeriod(id));
        model.addAttribute("myApplication", myApplication.orElse(null));
        model.addAttribute("uniformApplicationRequest", request);
        model.addAttribute("uniformSizes", UniformSize.values());
        if (isAdmin(authentication)) {
            model.addAttribute("applicationSummary", applicationService.getSummary(id));
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
