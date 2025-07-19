package com.storypass.storypass.dto;

public record GameRoomDto(Long id,
                          String title,
                          boolean isPublic,
                          int currentPlayerCount,
                          int maxPlayers,
                          int timeLimitPerTurnInSeconds,
                          int turnsPerPlayer) {}