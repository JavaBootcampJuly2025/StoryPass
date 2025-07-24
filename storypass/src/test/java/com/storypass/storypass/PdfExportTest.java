package com.storypass.storypass;

import com.storypass.storypass.dto.FullStoryDto;
import com.storypass.storypass.dto.StoryLineDto;
import com.storypass.storypass.service.PdfExportService;
import com.storypass.storypass.service.StoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PdfExportTest {

    private PdfExportService pdfExportService;

    @BeforeEach
    void setUp() {
        var storyService = Mockito.mock(StoryService.class);

        Mockito.when(storyService.getFullStoryById(Mockito.anyLong())).thenReturn(
                new FullStoryDto("Humpty Dumpty",
                        List.of("John Doe", "Ivan Petrov", "Max Mustermann"),
                        List.of(
                                new StoryLineDto("Humpty Dumpty sat on a wall.", "John"),
                                new StoryLineDto("Humpty Dumpty had a great fall.", "Ivan"),
                                new StoryLineDto("All the king's horses and all the king's men", "Max"),
                                new StoryLineDto("Couldn't put Humpty together again.", "John")
                        ))
        );

        pdfExportService = new PdfExportService(storyService);
    }

    @Test
    void testSuccess() {
        Assertions.assertDoesNotThrow(() -> {
                byte[] data = pdfExportService.generatePdfForStory(1L);

                File file
                        //= new File("HumptyDumpty.pdf"); // Use this is you want to get the file locally!
                        = File.createTempFile("test", "HumptyDumpty.pdf");

                var fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(data);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        );
    }
}
