package com.gukbabfc.home.controller;

import com.gukbabfc.home.service.HomeDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * 메인 화면 요청과 로그인 상태 표시를 담당합니다.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeDashboardService homeDashboardService;

    @GetMapping("/")
    public String home(Principal principal, Model model) {
        boolean signedIn = principal != null;
        model.addAttribute("signedIn", signedIn);
        model.addAttribute("username", signedIn ? principal.getName() : null);
        if (signedIn) {
            model.addAttribute("dashboard", homeDashboardService.getDashboard(principal.getName()));
        }
        return "home";
    }
}
