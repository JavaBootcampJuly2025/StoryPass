package com.storypass.storypass.dto;

import com.storypass.storypass.model.Status;

public record GameRoomDto(Long id,
                          String title,
                          boolean isPublic,
                          Status status,
                          int currentPlayerCount,
                          int maxPlayers,
                          String ownerNickname,
                          int timeLimitPerTurnInSeconds,
                          int turnsPerPlayer) {}