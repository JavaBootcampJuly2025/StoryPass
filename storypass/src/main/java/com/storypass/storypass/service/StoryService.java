package com.storypass.storypass.service;

import com.storypass.storypass.dto.FullStoryDto;
import com.storypass.storypass.dto.StoryLineDto;
import com.storypass.storypass.exception.ResourceNotFoundException;
import com.storypass.storypass.model.Story;
import com.storypass.storypass.model.User;
import com.storypass.storypass.repository.StoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoryService {

    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Transactional(readOnly = true)
    public FullStoryDto getFullStoryById(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story with ID " + storyId + " not found"));

        List<String> participantNicknames = story.getParticipants().stream()
                .map(User::getNickname)
                .collect(Collectors.toList());

        List<StoryLineDto> storyLines = story.getStoryLines().stream()
                .sorted(Comparator.comparingInt(sl -> sl.getSequenceNumber()))
                .map(line -> new StoryLineDto(line.getText(), line.getAuthor().getNickname()))
                .collect(Collectors.toList());

        return new FullStoryDto(story.getTitle(), participantNicknames, storyLines);
    }
}