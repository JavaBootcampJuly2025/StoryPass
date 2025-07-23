package com.storypass.storypass;

import com.storypass.storypass.service.PdfExportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootTest(classes = PdfExportService.class)
public class PdfExportTest {

    @Autowired
    PdfExportService pdfExportService;

    @Test
    void test() throws IOException {
        byte[] data = pdfExportService.generatePdfForStory(1L);

        File file = new File("StoryPass.pdf");

        var fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
