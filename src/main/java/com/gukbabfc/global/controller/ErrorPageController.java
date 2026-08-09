package com.gukbabfc.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 접근 거부 등 사용자에게 보여줄 공통 오류 화면을 제공합니다.
 */
@Controller
public class ErrorPageController {

    @RequestMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
