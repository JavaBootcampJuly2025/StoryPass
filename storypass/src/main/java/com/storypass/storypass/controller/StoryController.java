
package com.storypass.storypass.controller;

import com.storypass.storypass.service.PdfExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final PdfExportService pdfExportService;

    public StoryController(PdfExportService pdfExportService) {
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportStoryAsPdf(@PathVariable Long id) {
        byte[] pdfBytes = pdfExportService.generatePdfForStory(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=story-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}