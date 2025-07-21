package com.storypass.storypass.controller;

import com.storypass.storypass.dto.CreateRoomRequest;
import com.storypass.storypass.dto.GameRoomDto;
import com.storypass.storypass.dto.GameStateDto;
import com.storypass.storypass.dto.JoinPrivateRoomRequest;
import com.storypass.storypass.model.User;
import com.storypass.storypass.service.GameRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public GameRoomDto createGameRoom(@RequestBody CreateRoomRequest roomRequest,
                                      @AuthenticationPrincipal User creator) {
        return gameRoomService.createNewRoom(roomRequest, creator);
    }

    @DeleteMapping("/{id}")
    public void deleteRoomById(@PathVariable Long id,
                               @AuthenticationPrincipal User user) {
        gameRoomService.deleteRoomById(id, user);
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
    public ResponseEntity<GameRoomDto> updateRoom(@PathVariable Long id,
                                                  @AuthenticationPrincipal User user,
                                                  @RequestBody CreateRoomRequest roomRequest) {
        GameRoomDto gameRoomDTO = gameRoomService.updateRoomById(id, user, roomRequest);
        return ResponseEntity.ok(gameRoomDTO);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<GameRoomDto> joinRoom(@PathVariable Long id,
                                                @AuthenticationPrincipal User user,
                                                @RequestBody(required = false) JoinPrivateRoomRequest joinRequest) {

        GameRoomDto gameRoomDTO = gameRoomService.joinRoom(id, user, joinRequest);
        return ResponseEntity.ok(gameRoomDTO);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<GameRoomDto> leaveRoom(@PathVariable Long id,
                                                @AuthenticationPrincipal User user) {

        GameRoomDto gameRoomDto = gameRoomService.leaveRoom(id, user);
        return ResponseEntity.ok(gameRoomDto);
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<GameStateDto> getGameState(@PathVariable Long id) {
        GameStateDto gameStateDto = gameRoomService.getGameState(id);
        return ResponseEntity.ok(gameStateDto);
    }
}
