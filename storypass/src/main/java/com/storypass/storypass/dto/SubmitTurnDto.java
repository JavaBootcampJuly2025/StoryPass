package com.storypass.storypass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitTurnDto(
        @NotBlank(message = "Your sentence cannot be empty")
        @Size(max = 250, message = "Sentence cannot be longer than 250 characters")
        String text
) {}