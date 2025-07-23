package com.storypass.storypass.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/export")
public class PdfExportController {
    @GetMapping("pdf")
    public ResponseEntity<byte[]> exportToPdf()
    {
        byte[] pdfFile = null;

        final HttpHeaders httpHeaders = new HttpHeaders(MultiValueMap.fromSingleValue(Map.of(
                HttpHeaders.CONTENT_DISPOSITION, ("attachment; filename=" + "StoryPass.pdf") // TODO: Add story name!
        )));

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache().sMaxAge(0L, TimeUnit.SECONDS))
                .contentLength(000L)
                .contentType(MediaType.APPLICATION_PDF)
                .headers(httpHeaders)
                .body(pdfFile);
    }
}
