package com.jch.spring_web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
    @ExceptionHandler
    public String handleEX(Exception exception) {
        return exception.getLocalizedMessage() + exception.getClass().getCanonicalName();
    }
}
