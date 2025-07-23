package com.storypass.storypass.dto;

import java.util.List;

public class GameStateDto {
    private String lastLine;
    private String currentPlayerNickname;
    private int timeLeftSeconds;
    private String ownerNickname;
    private String status;

    // New field: list of players in the room
    private List<PlayerDto> players;

    public GameStateDto() {}

    public GameStateDto(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }

    public GameStateDto(String lastLine, String currentPlayerNickname, int timeLeftSeconds, String ownerNickname, String status, List<PlayerDto> players) {
        this.lastLine = lastLine;
        this.currentPlayerNickname = currentPlayerNickname;
        this.timeLeftSeconds = timeLeftSeconds;
        this.ownerNickname = ownerNickname;
        this.status = status;
        this.players = players;
    }

    public GameStateDto(String lastLine, String currentPlayerNickname, int timeLeftSeconds) {
        this.lastLine = lastLine;
        this.currentPlayerNickname = currentPlayerNickname;
        this.timeLeftSeconds = timeLeftSeconds;
    }

    // Getters and setters
    public String getLastLine() {
        return lastLine;
    }

    public void setLastLine(String lastLine) {
        this.lastLine = lastLine;
    }

    public String getCurrentPlayerNickname() {
        return currentPlayerNickname;
    }

    public void setCurrentPlayerNickname(String currentPlayerNickname) {
        this.currentPlayerNickname = currentPlayerNickname;
    }

    public int getTimeLeftSeconds() {
        return timeLeftSeconds;
    }

    public void setTimeLeftSeconds(int timeLeftSeconds) {
        this.timeLeftSeconds = timeLeftSeconds;
    }

    public String getOwnerNickname() {
        return ownerNickname;
    }

    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PlayerDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDto> players) {
        this.players = players;
    }

    private int maxPlayers;

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

}
