package com.gukbabfc.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * 애플리케이션 전역 예외를 HTTP 상태와 오류 화면으로 변환합니다.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException exception) {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }
}
