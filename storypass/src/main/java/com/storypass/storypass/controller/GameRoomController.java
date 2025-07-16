package com.storypass.storypass.controller;

import com.storypass.storypass.dto.CreateRoomRequest;
import com.storypass.storypass.dto.GameRoomDto;
import com.storypass.storypass.service.GameRoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class GameRoomController {

    private final GameRoomService gameRoomService;

    public GameRoomController(GameRoomService gameRoomService) {
        this.gameRoomService = gameRoomService;
    }

    @PostMapping
    public GameRoomDto createGameRoom(@RequestBody CreateRoomRequest roomRequest) {
        return gameRoomService.createNewRoom(roomRequest);
    }

    @GetMapping
    public List<GameRoomDto> getGameRooms() {
        return gameRoomService.getAllRooms();
    }
}