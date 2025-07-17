package com.storypass.storypass.controller;

import com.storypass.storypass.dto.CreateRoomRequest;
import com.storypass.storypass.dto.GameRoomDto;
import com.storypass.storypass.service.GameRoomService;
import org.springframework.http.ResponseEntity;
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

    @DeleteMapping("/{id}")
    public void deleteRoomById(@PathVariable Long id) {
        gameRoomService.deleteRoomById(id);
    }

    @GetMapping
    public List<GameRoomDto> getGameRooms() {
        return gameRoomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameRoomDto> getRoomById(@PathVariable Long id) {
        GameRoomDto gameRoomDTO = gameRoomService.getRoomById(id);
        return ResponseEntity.ok(gameRoomDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameRoomDto> updateRoom(@PathVariable Long id, @RequestBody CreateRoomRequest roomRequest) {
        GameRoomDto gameRoomDTO = gameRoomService.updateRoomById(id, roomRequest);
        return ResponseEntity.ok(gameRoomDTO);
    }
}