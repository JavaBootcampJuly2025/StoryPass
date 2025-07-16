package com.storypass.storypass.dto;

import java.time.LocalDateTime;

public record StorySummaryDto(Long storyId, String title, LocalDateTime createdAt) {}