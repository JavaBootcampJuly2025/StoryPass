package com.storypass.storypass.service;

import com.storypass.storypass.dto.CreateRoomRequest;
import com.storypass.storypass.dto.GameRoomDto;
import com.storypass.storypass.exception.ResourceNotFoundException;
import com.storypass.storypass.model.GameRoom;
import com.storypass.storypass.model.Story;
import com.storypass.storypass.repository.GameRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameRoomService {

    private final GameRoomRepository roomRepository;

    public GameRoomService(GameRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // return a list of GameRoomDTOs each being converted from game room entities
    public List<GameRoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GameRoomDto getRoomById(Long id) {
        GameRoom foundRoom = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + id + " not found"));
        return convertToDTO(foundRoom);
    }

    @Transactional
    public GameRoomDto createNewRoom(CreateRoomRequest roomRequest) {
        GameRoom newRoom = convertToEntity(roomRequest);

        // give the room a new default story
        Story story = new Story();
        newRoom.setStory(story);

        roomRepository.save(newRoom);

        return convertToDTO(newRoom);
    }

    //convert GameRoomDTO to GameRoom entity
    private GameRoom convertToEntity(CreateRoomRequest roomRequest) {
        GameRoom gameRoom = new GameRoom();
        gameRoom.setTitle(roomRequest.title());
        gameRoom.setRoomCode(roomRequest.roomCode());
        gameRoom.setPublic(roomRequest.isPublic());
        gameRoom.setMaxPlayers(roomRequest.maxPlayers());
        gameRoom.setTimeLimitPerTurnInSeconds(roomRequest.timeLimitPerTurnInSeconds());
        gameRoom.setTurnsPerPlayer(roomRequest.turnsPerPlayer());

        return gameRoom;
    }

    //convert GameRoom entity to GameRoomDTO
    private GameRoomDto convertToDTO(GameRoom gameRoom) {
        return new GameRoomDto(
                gameRoom.getId(),
                gameRoom.getTitle(),
                gameRoom.isPublic(),
                gameRoom.getMaxPlayers(),
                gameRoom.getCurrentPlayerCount()
                );
    }
}
