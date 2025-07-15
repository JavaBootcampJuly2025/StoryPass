package com.storypass.storypass.dto;

import jakarta.validation.constraints.NotBlank;

public class JoinPrivateRoomRequest {
    @NotBlank(message = "Room code cannot be empty")
    private String roomCode;

    // Getter and Setter
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
}