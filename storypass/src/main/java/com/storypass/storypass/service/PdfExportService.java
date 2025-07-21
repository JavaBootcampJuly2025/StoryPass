package com.storypass.storypass.service;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Log
@Service
public class PdfExportService {
    public byte[] generatePdf()
    {
        try {
            String text = "One upon a time..."; // TODO: Init with the actual story text!

            InputStream templateStream = getClass().getResourceAsStream("/jasper-reports/templates/StoryPass.jrxml");

            var compiledTemplate = JasperCompileManager.compileReport(templateStream);

            var params = new HashMap<>(Map.<String, Object>of(
                    "title", "My Story", // TODO: Add title!
                    "text", text
            ));

            var jasperPrint = JasperFillManager.fillReport(compiledTemplate, params);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.severe("JasperReport error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
