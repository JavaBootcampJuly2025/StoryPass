package com.storypass.storypass.service;

import com.storypass.storypass.dto.CreateRoomRequest;
import com.storypass.storypass.dto.GameRoomDto;
import com.storypass.storypass.dto.GameStateDto;
import com.storypass.storypass.exception.ResourceNotFoundException;
import com.storypass.storypass.model.GameRoom;
import com.storypass.storypass.model.Status;
import com.storypass.storypass.model.Story;
import com.storypass.storypass.repository.GameRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameRoomService {

    private final GameRoomRepository roomRepository;

    public GameRoomService(GameRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional
    public GameRoomDto createNewRoom(CreateRoomRequest roomRequest) {
        GameRoom newRoom = convertToEntity(roomRequest);
        Story story = new Story();
        newRoom.setStory(story);
        newRoom.setStatus(Status.WAITING_FOR_PLAYERS);
        roomRepository.save(newRoom);
        return convertToDTO(newRoom);
    }

    public void deleteRoomById(Long id) {
        roomRepository.deleteById(id);
    }

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
    public GameRoomDto updateRoomById(Long id, CreateRoomRequest roomRequest) {
        GameRoom roomToUpdate = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + id + " not found"));

        roomToUpdate.setTitle(roomRequest.title());
        roomToUpdate.setRoomCode(roomRequest.roomCode());
        roomToUpdate.setPublic(roomRequest.isPublic());
        roomToUpdate.setMaxPlayers(roomRequest.maxPlayers());
        roomToUpdate.setTimeLimitPerTurnInSeconds(roomRequest.timeLimitPerTurnInSeconds());
        roomToUpdate.setTurnsPerPlayer(roomRequest.turnsPerPlayer());

        GameRoom updatedRoom = roomRepository.save(roomToUpdate);

        return convertToDTO(updatedRoom);
    }

    @Transactional(readOnly = true)
    public GameStateDto getGameState(Long roomId) {
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        String lastLine = room.getStory() != null && room.getStory().getStoryLines() != null && !room.getStory().getStoryLines().isEmpty()
                ? room.getStory().getStoryLines().get(room.getStory().getStoryLines().size() - 1).getText()
                : "";

        String currentPlayerNickname = room.getCurrentPlayer() != null
                ? room.getCurrentPlayer().getNickname()
                : "";

        int timeLeft = room.getTimeLeftForCurrentTurnInSeconds();

        return new GameStateDto(lastLine, currentPlayerNickname, timeLeft);
    }

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
