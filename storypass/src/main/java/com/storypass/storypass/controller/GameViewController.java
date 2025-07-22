package com.storypass.storypass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GameViewController {

    @GetMapping("/game")
    public String gameView(@RequestParam Long roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "game";
    }
}
