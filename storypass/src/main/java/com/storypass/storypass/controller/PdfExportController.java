package com.storypass.storypass.controller;

import com.storypass.storypass.service.PdfExportService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;
@RestController
@RequestMapping("/api/export")
public class PdfExportController {

    private final PdfExportService pdfExportService;

    public PdfExportController(PdfExportService pdfExportService) {
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("pdf")
    public ResponseEntity<byte[]> exportToPdf(@RequestParam Long storyId) {
        byte[] pdfFile = pdfExportService.generatePdfForStory(storyId);

        HttpHeaders httpHeaders = new HttpHeaders();
        String storyTitle = pdfExportService.getStoryTitleById(storyId);
        String sanitizedTitle = storyTitle.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + sanitizedTitle + ".pdf");

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache().sMaxAge(0L, TimeUnit.SECONDS))
                .contentLength(pdfFile.length)
                .contentType(MediaType.APPLICATION_PDF)
                .headers(httpHeaders)
                .body(pdfFile);
    }
}

