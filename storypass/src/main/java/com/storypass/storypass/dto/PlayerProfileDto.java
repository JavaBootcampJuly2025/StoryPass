package com.storypass.storypass.dto;

import java.util.List;

public class PlayerProfileDto {
    private String nickname;
    private List<StorySummaryDto> stories;

    // Constructors, Getters and Setters
    public PlayerProfileDto(String nickname, List<StorySummaryDto> stories) {
        this.nickname = nickname;
        this.stories = stories;
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public List<StorySummaryDto> getStories() { return stories; }
    public void setStories(List<StorySummaryDto> stories) { this.stories = stories; }
}