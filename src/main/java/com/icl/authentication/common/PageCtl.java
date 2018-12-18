package com.icl.authentication.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class PageCtl {

    @GetMapping("/")
    public String direct() {
        log.trace("Default page requested, rendering index.");
        return "index";
    }
}
