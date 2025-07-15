package com.storypass.storypass.dto;

import java.time.LocalDateTime;

public class StorySummaryDto {
    private Long storyId;
    private String title;
    private LocalDateTime createdAt;

    // Constructors, Getters and Setters
    public StorySummaryDto(Long storyId, String title, LocalDateTime createdAt) {
        this.storyId = storyId;
        this.title = title;
        this.createdAt = createdAt;
    }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}