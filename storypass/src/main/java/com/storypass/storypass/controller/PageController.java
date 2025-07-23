package com.storypass.storypass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/playerprofile")
    public String playerProfilePage() {
        return "playerprofile";
    }
}
