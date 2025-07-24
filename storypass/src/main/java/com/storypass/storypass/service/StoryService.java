package com.storypass.storypass.service;

import com.storypass.storypass.dto.FullStoryDto;
import com.storypass.storypass.dto.StoryLineDto;
import com.storypass.storypass.exception.ResourceNotFoundException;
import com.storypass.storypass.model.Story;
import com.storypass.storypass.model.User;
import com.storypass.storypass.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${together.api.key}")
    private String togetherApiKey;

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

    public String generateTitle(String storyText) {
        String prompt = "Generate an adventurous, attractive and engaging title with no more than 5 words that best suits the story below:\n\n" + storyText;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(togetherApiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "meta-llama/Llama-3.3-70B-Instruct-Turbo-Free",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.together.xyz/v1/chat/completions",
                    request,
                    Map.class
            );

            List<?> choices = (List<?>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) choice.get("message");
                if (message != null) {
                    return ((String) message.get("content")).trim().replaceAll("\"", "");
                }
            }

            throw new RuntimeException("No valid response from Together.xyz API");

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate title via Together.xyz API", e);
        }
    }
}
