package com.storypass.storypass.dto;

import java.util.List;

public record FullStoryDto(String title, List<String> participants, List<StoryLineDto> lines) {}