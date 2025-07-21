package com.storypass.storypass.service;

import com.storypass.storypass.dto.CreateRoomRequest;
import com.storypass.storypass.dto.GameRoomDto;
import com.storypass.storypass.dto.GameStateDto;
import com.storypass.storypass.dto.JoinPrivateRoomRequest;
import com.storypass.storypass.exception.*;
import com.storypass.storypass.model.GameRoom;
import com.storypass.storypass.model.Status;
import com.storypass.storypass.model.Story;
import com.storypass.storypass.model.User;
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
    public GameRoomDto createNewRoom(CreateRoomRequest roomRequest,
                                     User owner) {
        GameRoom newRoom = convertToEntity(roomRequest);

        // add the owner and add him as a player
        newRoom.setOwner(owner);
        newRoom.getPlayers().add(owner);
        newRoom.setCurrentPlayerCount(1);
        newRoom.setCurrentPlayer(owner);

        Story story = new Story();
        newRoom.setStory(story);
        newRoom.setStatus(Status.WAITING_FOR_PLAYERS);

        roomRepository.save(newRoom);
        return convertToDTO(newRoom);
    }

    public void deleteRoomById(Long roomId, User user) {
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        if(!room.getOwner().equals(user)) {
            throw new NoAccessException("You are not the owner of this room");
        }

        roomRepository.deleteById(roomId);
    }

    public List<GameRoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GameRoomDto getRoomById(Long roomId) {
        GameRoom foundRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));
        return convertToDTO(foundRoom);
    }

    @Transactional
    public GameRoomDto updateRoomById(Long roomId, User user, CreateRoomRequest roomRequest) {
        GameRoom roomToUpdate = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        if(!roomToUpdate.getOwner().equals(user)) {
            throw new NoAccessException("Only the owner can update the room");
        }

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

    @Transactional
    public GameRoomDto joinRoom(Long roomId, User user, JoinPrivateRoomRequest joinRequest) {

        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        if (!room.isPublic() && joinRequest == null ) {
            throw new NoAccessException("Room is private and requires a room code to join");
        }
        if (!room.isPublic() && !room.getRoomCode().equals(joinRequest.roomCode())) {
            throw new NoAccessException("Room code is incorrect");
        }
        if (!room.getStatus().equals(Status.WAITING_FOR_PLAYERS)) {
            throw new CurrentStatusException("Room is not waiting for new players");
        }
        if (room.getCurrentPlayerCount() >= room.getMaxPlayers()) {
            throw new RoomFullException("Room is full");
        }
        if(room.getPlayers().contains(user)) {
            throw new DuplicateResourceException("User is already in the room"); // not sure if I can use this exception type here
        }

        room.getPlayers().add(user);
        room.setCurrentPlayerCount(room.getCurrentPlayerCount() + 1);

        GameRoom updatedRoom = roomRepository.save(room);
        return convertToDTO(updatedRoom);
    }

    @Transactional
    public GameRoomDto leaveRoom(Long roomId, User user) {
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        if(!room.getPlayers().contains(user)) {
            throw new ResourceNotFoundException("User is not in the room with id: " + roomId);
        }
        if(room.getOwner().equals(user) && room.getCurrentPlayerCount() > 1) {
            throw new CurrentStatusException("Owner cannot leave the room if there are other players");
        }

        room.getPlayers().remove(user);
        room.setCurrentPlayerCount(room.getCurrentPlayerCount() - 1);

        GameRoom updatedRoom = roomRepository.save(room);
        return convertToDTO(updatedRoom);
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
                gameRoom.getCurrentPlayerCount(),
                gameRoom.getMaxPlayers(),
                gameRoom.getOwner().getNickname(),
                gameRoom.getTimeLimitPerTurnInSeconds(),
                gameRoom.getTurnsPerPlayer()
        );
    }
}
