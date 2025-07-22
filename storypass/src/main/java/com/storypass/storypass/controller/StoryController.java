package com.storypass.storypass.controller;

import com.storypass.storypass.dto.FullStoryDto;
import com.storypass.storypass.service.StoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullStoryDto> getFullStory(@PathVariable Long id) {
        FullStoryDto fullStory = storyService.getFullStoryById(id);
        return ResponseEntity.ok(fullStory);
    }
}