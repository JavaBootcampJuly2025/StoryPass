package com.storypass.storypass.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Login cannot be empty")
        String login,

        @NotBlank(message = "Password cannot be empty")
        String password
) {}