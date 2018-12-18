package com.icl.authentication.token;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenResource {

    @GetMapping("/{application}")
    public String getToken(LoginRequest request, @PathVariable("application") String application) {
        return null;
    }
}
