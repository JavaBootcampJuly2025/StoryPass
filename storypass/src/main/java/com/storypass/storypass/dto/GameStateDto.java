package com.storypass.storypass.dto;

public record GameStateDto(String lastLine, String currentPlayerNickname, int timeLeft, int currentRound, int totalRounds) {}