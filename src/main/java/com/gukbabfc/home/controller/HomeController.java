package com.gukbabfc.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * 메인 화면 요청과 로그인 상태 표시를 담당합니다.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Principal principal, Model model) {
        model.addAttribute("signedIn", principal != null);
        model.addAttribute("username", principal == null ? null : principal.getName());
        return "home";
    }
}
