package com.storypass.storypass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "Login cannot be empty")
        @Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
        String login,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
        String password,

        @NotBlank(message = "Nickname cannot be empty")
        @Size(min = 3, max = 15, message = "Nickname must be between 3 and 15 characters")
        String nickname
) {}