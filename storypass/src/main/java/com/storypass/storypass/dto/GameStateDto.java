package com.storypass.storypass.dto;

public class GameStateDto {
    private String lastLine;
    private String currentPlayerNickname;
    private int timeLeftSeconds;

    public GameStateDto() {
    }

    public GameStateDto(String lastLine, String currentPlayerNickname, int timeLeftSeconds) {
        this.lastLine = lastLine;
        this.currentPlayerNickname = currentPlayerNickname;
        this.timeLeftSeconds = timeLeftSeconds;
    }

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
}
