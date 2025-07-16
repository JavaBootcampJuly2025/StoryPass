package com.storypass.storypass.dto;

import java.util.List;

public record FullStoryDto(String title, List<StoryLineDto> lines) {}