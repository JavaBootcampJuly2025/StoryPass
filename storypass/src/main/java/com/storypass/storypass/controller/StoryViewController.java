package com.storypass.storypass.controller;

import com.storypass.storypass.dto.FullStoryDto;
import com.storypass.storypass.service.StoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stories")
public class StoryViewController {

    private final StoryService storyService;

    public StoryViewController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping("/{id}/view")
    public String viewStory(@PathVariable Long id, Model model) {
        FullStoryDto story = storyService.getFullStoryById(id);
        model.addAttribute("story", story);
        model.addAttribute("storyId", id);
        return "results"; // шаблон results.html
    }
}
