package com.storypass.storypass.dto;

public class GameRoomDto {
    private Long id;
    private String title;
    private boolean isPublic;
    private int currentPlayerCount;
    private int maxPlayers;

    // Constructors, Getters and Setters
    public GameRoomDto() {}

    public GameRoomDto(Long id, String title, boolean isPublic, int currentPlayerCount, int maxPlayers) {
        this.id = id;
        this.title = title;
        this.isPublic = isPublic;
        this.currentPlayerCount = currentPlayerCount;
        this.maxPlayers = maxPlayers;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
    public int getCurrentPlayerCount() { return currentPlayerCount; }
    public void setCurrentPlayerCount(int currentPlayerCount) { this.currentPlayerCount = currentPlayerCount; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
}