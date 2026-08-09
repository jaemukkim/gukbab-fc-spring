package com.gukbabfc.freeboard.controller;

import com.gukbabfc.freeboard.dto.FreeBoardCreateRequest;
import com.gukbabfc.freeboard.dto.FreeBoardUpdateRequest;
import com.gukbabfc.freeboard.service.FreeBoardService;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/freeboards")
public class FreeBoardController {

    private final FreeBoardService freeBoardService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword,
                       Model model) {
        var postPage = freeBoardService.getPosts(page, keyword);
        model.addAttribute("postPage", postPage);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("keyword", keyword.trim());
        return "freeboard/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication authentication, Model model) {
        model.addAttribute("post", freeBoardService.getPost(id));
        model.addAttribute("canManage", freeBoardService.canManage(id, authentication.getName()));
        return "freeboard/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("freeBoardCreateRequest", new FreeBoardCreateRequest());
        return "freeboard/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute FreeBoardCreateRequest freeBoardCreateRequest,
                         BindingResult bindingResult,
                         Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "freeboard/form";
        }
        Long id = freeBoardService.createPost(authentication.getName(), freeBoardCreateRequest);
        return "redirect:/freeboards/" + id;
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Authentication authentication, Model model) {
        model.addAttribute("postId", id);
        model.addAttribute("freeBoardUpdateRequest",
                freeBoardService.getUpdateRequest(id, authentication.getName()));
        return "freeboard/edit-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute FreeBoardUpdateRequest freeBoardUpdateRequest,
                         BindingResult bindingResult,
                         Authentication authentication,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", id);
            return "freeboard/edit-form";
        }
        freeBoardService.updatePost(id, authentication.getName(), freeBoardUpdateRequest);
        return "redirect:/freeboards/" + id + "?updated";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        freeBoardService.deletePost(id, authentication.getName());
        return "redirect:/freeboards?deleted";
    }
}
