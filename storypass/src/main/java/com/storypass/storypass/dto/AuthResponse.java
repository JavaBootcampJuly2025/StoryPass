package com.storypass.storypass.dto;

public class AuthResponse {

    private String token;
    private String nickname;

    public AuthResponse(String token, String nickname) {
        this.token = token;
        this.nickname = nickname;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}