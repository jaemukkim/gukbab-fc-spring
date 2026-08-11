package com.gukbabfc.schedule.controller;

import com.gukbabfc.schedule.dto.ScheduleCreateRequest;
import com.gukbabfc.schedule.dto.ParticipationRequest;
import com.gukbabfc.schedule.dto.ScheduleListResponse;
import com.gukbabfc.schedule.dto.ScheduleUpdateRequest;
import com.gukbabfc.schedule.service.ScheduleService;
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
 * 풋살 일정 목록과 관리자 CRUD 화면 요청을 처리합니다.
 */
@Controller
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public String list(Model model) {
        ScheduleListResponse schedules = scheduleService.getSchedules();
        model.addAttribute("upcomingSchedules", schedules.upcomingSchedules());
        model.addAttribute("pastSchedules", schedules.pastSchedules());
        return "schedule/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication authentication, Model model) {
        model.addAttribute("schedule", scheduleService.getSchedule(id));
        model.addAttribute("participation",
                scheduleService.getParticipationSummary(id, authentication.getName()));
        model.addAttribute("participationRequest", new ParticipationRequest());
        return "schedule/detail";
    }

    @PostMapping("/{id}/participation")
    public String respond(@PathVariable Long id,
                          @Valid @ModelAttribute ParticipationRequest participationRequest,
                          BindingResult bindingResult,
                          Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "redirect:/schedules/" + id + "?invalidParticipation";
        }
        scheduleService.respondToSchedule(
                id,
                authentication.getName(),
                participationRequest.getStatus()
        );
        return "redirect:/schedules/" + id + "?participationUpdated";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("scheduleCreateRequest", new ScheduleCreateRequest());
        return "schedule/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute ScheduleCreateRequest scheduleCreateRequest,
                         BindingResult bindingResult,
                         Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "schedule/form";
        }
        Long id = scheduleService.createSchedule(authentication.getName(), scheduleCreateRequest);
        return "redirect:/schedules/" + id;
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {
        model.addAttribute("scheduleId", id);
        model.addAttribute("scheduleUpdateRequest", scheduleService.getUpdateRequest(id));
        return "schedule/edit-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute ScheduleUpdateRequest scheduleUpdateRequest,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("scheduleId", id);
            return "schedule/edit-form";
        }
        scheduleService.updateSchedule(id, scheduleUpdateRequest);
        return "redirect:/schedules/" + id + "?updated";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/schedules?deleted";
    }
}
