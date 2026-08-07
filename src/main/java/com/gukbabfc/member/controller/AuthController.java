package com.gukbabfc.member.controller;

import com.gukbabfc.member.dto.SignupRequest;
import com.gukbabfc.member.exception.SignupException;
import com.gukbabfc.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupRequest signupRequest,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member/signup";
        }

        try {
            memberService.signup(signupRequest);
        } catch (SignupException exception) {
            bindingResult.rejectValue(exception.getField(), "signup", exception.getMessage());
            return "member/signup";
        }
        return "redirect:/login?signup";
    }

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }
}
