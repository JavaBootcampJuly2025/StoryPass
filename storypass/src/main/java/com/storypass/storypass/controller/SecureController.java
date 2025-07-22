package com.storypass.storypass.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureController {

    @GetMapping("/api/secure-data")
    public String secureData() {
        return "This is protected data available only with a valid JWT!";
    }
}
