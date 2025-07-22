package com.storypass.storypass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LobbyController {

    @GetMapping("/")
    public String index() {

        return "redirect:/index.html";
    }

    @GetMapping("/lobby")
    public String lobby() {

        return "lobby";
    }
}
