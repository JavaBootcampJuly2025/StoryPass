package com.storypass.storypass.service;

import com.storypass.storypass.dto.PlayerProfileDto;
import com.storypass.storypass.dto.StorySummaryDto;
import com.storypass.storypass.model.Story;
import com.storypass.storypass.model.User;
import com.storypass.storypass.repository.StoryRepository;
import com.storypass.storypass.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    public ProfileService(UserRepository userRepository, StoryRepository storyRepository) {
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
    }

    public PlayerProfileDto getProfile(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Story> stories = storyRepository.findAllByParticipants_Id(user.getId());

        List<StorySummaryDto> storyDtos = stories.stream()
                .map(s -> new StorySummaryDto(
                        s.getId(),
                        s.getTitle(),
                        s.getCreatedAt()
                ))
                .toList();

        return new PlayerProfileDto(user.getNickname(), storyDtos);
    }
}
