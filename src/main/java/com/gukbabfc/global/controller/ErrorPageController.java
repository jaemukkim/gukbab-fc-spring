package com.gukbabfc.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController {

    @RequestMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
