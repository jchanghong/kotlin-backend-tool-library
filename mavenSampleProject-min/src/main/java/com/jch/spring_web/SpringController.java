package com.jch.spring_web;

import com.jch.jpa.JpaEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/mvc")
@Validated
public class SpringController {
    @PostMapping("/post")
    public String springPost(@Min(2) @RequestParam Long id, @Valid @RequestBody JpaEntity entity) {
        return "ok";
    }
}
