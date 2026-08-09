package com.gukbabfc.member.controller;

import com.gukbabfc.member.dto.PlayerProfile;
import com.gukbabfc.member.dto.ProfileUpdateRequest;
import com.gukbabfc.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 내 프로필과 선수 목록 화면 요청을 처리합니다.
 */
@Controller
public class ProfileController {

    private final MemberService memberService;

    public ProfileController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/mypage")
    public String myPage(Authentication authentication, Model model) {
        model.addAttribute("profile", memberService.getProfile(authentication.getName()));
        return "member/mypage";
    }

    @GetMapping("/mypage/edit")
    public String editForm(Authentication authentication, Model model) {
        PlayerProfile profile = memberService.getProfile(authentication.getName());
        model.addAttribute("profileUpdateRequest", new ProfileUpdateRequest(
                profile.name(),
                profile.position(),
                profile.backNumber(),
                profile.introduction()
        ));
        return "member/profile-edit";
    }

    @PostMapping("/mypage/edit")
    public String update(@Valid @ModelAttribute ProfileUpdateRequest profileUpdateRequest,
                         BindingResult bindingResult,
                         Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "member/profile-edit";
        }
        memberService.updateProfile(authentication.getName(), profileUpdateRequest);
        return "redirect:/mypage?updated";
    }

    @GetMapping("/players")
    public String players(Model model) {
        model.addAttribute("players", memberService.getPlayers());
        return "member/players";
    }
}
