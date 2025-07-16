package com.storypass.storypass.dto;

import jakarta.validation.constraints.*;

public record CreateRoomRequest(
        @NotBlank(message = "Room title cannot be empty")
        @Size(min = 3, max = 30, message = "Title must be between 3 and 30 characters")
        String title,

        @NotNull(message = "Privacy setting is required")
        boolean isPublic,

        @Pattern(regexp = "^\\d{4}$", message = "Room code must be a 4-digit number")
        String roomCode,

        @NotNull(message = "Max players must be specified")
        @Min(value = 2, message = "Minimum 2 players required")
        @Max(value = 10, message = "Maximum 10 players allowed")
        int maxPlayers,

        @NotNull(message = "Time limit must be specified")
        @Min(value = 60, message = "Time limit must be at least 60 seconds")
        int timeLimitPerTurnInSeconds,

        @NotNull(message = "Number of turns must be specified")
        @Min(value = 1, message = "At least 1 turn per player is required")
        @Max(value = 10, message = "Maximum 10 turns per player allowed")
        int turnsPerPlayer
) {}