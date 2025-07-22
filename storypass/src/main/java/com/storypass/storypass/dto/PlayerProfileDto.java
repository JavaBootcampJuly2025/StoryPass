package com.storypass.storypass.dto;

import java.util.List;

public record PlayerProfileDto(String nickname, List<StorySummaryDto> stories) {}