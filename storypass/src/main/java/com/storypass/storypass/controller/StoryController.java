package com.storypass.storypass.controller;

import com.storypass.storypass.service.PdfExportService;
import com.storypass.storypass.service.StoryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final PdfExportService pdfExportService;
    private final StoryService storyService;

    public StoryController(PdfExportService pdfExportService, StoryService storyService) {
        this.pdfExportService = pdfExportService;
        this.storyService = storyService;
    }



//no need anymore :/
//    @GetMapping("/{id}/generate-title")
//    public ResponseEntity<String> generateTitle(@PathVariable Long id) {
//        var fullStory = storyService.getFullStoryById(id);
//
//
//        StringBuilder storyTextBuilder = new StringBuilder();
//        fullStory.lines().forEach(line -> storyTextBuilder.append(line.text()).append("\n"));
//
//        String generatedTitle = storyService.generateTitle(storyTextBuilder.toString());
//
//        return ResponseEntity.ok(generatedTitle);
//    }


    @PostMapping("/generate-title")
    public ResponseEntity<String> generateTitleFromText(@RequestBody Map<String, String> payload) {
        String inputText = payload.get("inputText");
        if (inputText == null || inputText.isBlank()) {
            return ResponseEntity.badRequest().body("Input text is required");
        }
        String generatedTitle = storyService.generateTitle(inputText);
        return ResponseEntity.ok(generatedTitle);
    }


}
