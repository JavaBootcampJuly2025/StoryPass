package com.storypass.storypass.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinPrivateRoomRequest(
        @NotBlank(message = "Room code cannot be empty")
        String roomCode
) {}