package com.storypass.storypass.controller;

import com.storypass.storypass.dto.SubmitTurnDto;
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
    public ResponseEntity<GameRoomDto> createGameRoom(@RequestBody CreateRoomRequest roomRequest,
                                                      @AuthenticationPrincipal User creator) {
        GameRoomDto createdRoom = gameRoomService.createNewRoom(roomRequest, creator);
        return ResponseEntity.ok(createdRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long id,
                                               @AuthenticationPrincipal User user) {
        gameRoomService.deleteRoomById(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GameRoomDto>> getGameRooms() {
        List<GameRoomDto> rooms = gameRoomService.getAllRooms();
        return ResponseEntity.ok(rooms);
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
        GameRoomDto updatedRoom = gameRoomService.updateRoomById(id, user, roomRequest);
        return ResponseEntity.ok(updatedRoom);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<GameRoomDto> joinRoom(@PathVariable Long id,
                                                @AuthenticationPrincipal User user,
                                                @RequestBody(required = false) JoinPrivateRoomRequest joinRequest) {

        // Improved join logic: If user already joined, just return the room DTO without error
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

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startGame(@PathVariable Long id,
                                          @AuthenticationPrincipal User user) {
        gameRoomService.startGame(id, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/turn")
    public ResponseEntity<Void> submitTurn(@PathVariable Long id,
                                           @AuthenticationPrincipal User user,
                                           @RequestBody SubmitTurnDto dto) {
        gameRoomService.submitTurn(id, user, dto);
        return ResponseEntity.ok().build();
    }
}
