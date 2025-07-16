package com.storypass.storypass.dto;

import jakarta.validation.constraints.*;

public class CreateRoomRequest {

    @NotBlank(message = "Room title cannot be empty")
    @Size(min = 3, max = 30, message = "Title must be between 3 and 30 characters")
    private String title;

    @NotNull(message = "Privacy setting is required")
    private boolean isPublic;

    @Pattern(regexp = "^\\d{4}$", message = "Room code must be a 4-digit number")
    private String roomCode;

    @NotNull(message = "Max players must be specified")
    @Min(value = 2, message = "Minimum 2 players required")
    @Max(value = 10, message = "Maximum 10 players allowed")
    private int maxPlayers;

    @NotNull(message = "Time limit must be specified")
    @Min(value = 60, message = "Time limit must be at least 60 seconds")
    // IMPORTANT: The service will need to additionally check that the value is 60, 90 or 120.
    private int timeLimitPerTurnInSeconds;

    @NotNull(message = "Number of turns must be specified")
    @Min(value = 1, message = "At least 1 turn per player is required")
    @Max(value = 10, message = "Maximum 10 turns per player allowed")
    private int turnsPerPlayer;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public int getTimeLimitPerTurnInSeconds() { return timeLimitPerTurnInSeconds; }
    public void setTimeLimitPerTurnInSeconds(int timeLimitPerTurnInSeconds) { this.timeLimitPerTurnInSeconds = timeLimitPerTurnInSeconds; }
    public int getTurnsPerPlayer() { return turnsPerPlayer; }
    public void setTurnsPerPlayer(int turnsPerPlayer) { this.turnsPerPlayer = turnsPerPlayer; }
}