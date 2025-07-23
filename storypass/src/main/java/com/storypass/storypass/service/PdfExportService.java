package com.storypass.storypass.service;

import com.storypass.storypass.dto.FullStoryDto;
import com.storypass.storypass.dto.StoryLineDto;
import lombok.extern.java.Log;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log
@Service
public class PdfExportService {

    private final StoryService storyService;

    public PdfExportService(StoryService storyService) {
        this.storyService = storyService;
    }

    public byte[] generatePdfForStory(Long storyId) {
        try {
            FullStoryDto story = storyService.getFullStoryById(storyId);

            String fullText = story.lines().stream()
                    .map(line -> line.authorNickname() + ": " + line.text())
                    .collect(Collectors.joining("\n"));

            InputStream templateStream = getClass().getResourceAsStream("/jasper-reports/templates/StoryPass.jrxml");

            JasperReport compiledTemplate = JasperCompileManager.compileReport(templateStream);

            Map<String, Object> params = new HashMap<>();
            params.put("title", story.title());
            params.put("text", fullText);

            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledTemplate, params);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.severe("JasperReport error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}