package com.storypass.storypass;

import com.storypass.storypass.model.Story;
import com.storypass.storypass.repository.StoryRepository;
import com.storypass.storypass.service.StoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoryServiceTests {

    @Mock StoryRepository storyRepository;

    @InjectMocks StoryService storyService;

    @Test
    void shouldReturnStoryById() {
        Story story = new Story();
        story.setId(1L);
        story.setTitle("test story");

        when(storyRepository.findById(1L)).thenReturn(Optional.of(story));

        assertEquals("test story", storyService.getFullStoryById(1L).title());
    }
}
