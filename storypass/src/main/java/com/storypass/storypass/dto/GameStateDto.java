package com.storypass.storypass.dto;

public class GameStateDto {
    private String lastLine;
    private String currentPlayerNickname;
    private int timeLeft;
    private int currentRound;
    private int totalRounds;

    // Constructors, Getters and Setters
    public GameStateDto(String lastLine, String currentPlayerNickname, int timeLeft, int currentRound, int totalRounds) {
        this.lastLine = lastLine;
        this.currentPlayerNickname = currentPlayerNickname;
        this.timeLeft = timeLeft;
        this.currentRound = currentRound;
        this.totalRounds = totalRounds;
    }

    //... getters and setters
}