package com.storypass.storypass.service;

import com.storypass.storypass.dto.*;
import com.storypass.storypass.exception.*;
import com.storypass.storypass.model.*;
import com.storypass.storypass.repository.GameRoomRepository;
import com.storypass.storypass.repository.StoryLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameRoomService {

    private final GameRoomRepository roomRepository;
    private final StoryLineRepository storyLineRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameRoomService(GameRoomRepository roomRepository,
                           StoryLineRepository storyLineRepository,
                           SimpMessagingTemplate messagingTemplate) {
        this.roomRepository = roomRepository;
        this.storyLineRepository = storyLineRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public GameRoomDto createNewRoom(CreateRoomRequest roomRequest, User owner) {
        GameRoom newRoom = convertToEntity(roomRequest);
        newRoom.setOwner(owner);
        newRoom.getPlayers().add(owner);
        newRoom.setCurrentPlayerCount(1);
        newRoom.setCurrentPlayer(owner);

        Story story = new Story();
        newRoom.setStory(story);

        newRoom.setStatus(Status.WAITING_FOR_PLAYERS);

        roomRepository.save(newRoom);
        broadcastRoomList();
        return convertToDTO(newRoom);
    }

    @Transactional
    public void skipTurn(GameRoom room) {
        List<User> players = new ArrayList<>(room.getPlayers());
        int currentIndex = players.indexOf(room.getCurrentPlayer());

        if (currentIndex == -1) return;

        int nextIndex = (currentIndex + 1) % players.size();
        boolean isNewRound = nextIndex == 0;

        if (isNewRound) {
            room.setTurnsPerPlayer(room.getTurnsPerPlayer() - 1);
            if (room.getTurnsPerPlayer() <= 0) {
                room.setStatus(Status.FINISHED);
                room.setCurrentPlayer(null);
            }
        }

        if (room.getStatus() != Status.FINISHED) {
            room.setCurrentPlayer(players.get(nextIndex));
            room.setTimeLeftForCurrentTurnInSeconds(room.getTimeLimitPerTurnInSeconds());
        }

        roomRepository.save(room);
        broadcastGameState(room);
    }


    @Transactional

    public void deleteRoomById(Long roomId, User user) {
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        if (!room.getOwner().equals(user)) {
            throw new NoAccessException("You are not the owner of this room");
        }

        roomRepository.deleteById(roomId);
        broadcastRoomList();
    }

    @Transactional(readOnly = true)
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
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        if (!room.getOwner().equals(user)) {
            throw new NoAccessException("Only the owner can update the room");
        }

        room.setTitle(roomRequest.title());

        room.setRoomCode(roomRequest.isPublic() ? null : roomRequest.roomCode());
        room.setPublic(roomRequest.isPublic());
        room.setMaxPlayers(roomRequest.maxPlayers());
        room.setTimeLimitPerTurnInSeconds(roomRequest.timeLimitPerTurnInSeconds());
        room.setTurnsPerPlayer(roomRequest.turnsPerPlayer());

        GameRoom updatedRoom = roomRepository.save(room);

        broadcastRoomList();

        return convertToDTO(updatedRoom);
    }

    @Transactional(readOnly = true)
    public GameStateDto getGameState(Long roomId, User currentUser) {
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        List<User> players = new ArrayList<>(room.getPlayers());
        List<StoryLine> lines = room.getStory().getStoryLines().stream()
                .sorted(Comparator.comparing(StoryLine::getId))
                .collect(Collectors.toList());


        String visibleLine = "";

        // Find current user index in player list
        int currentIndex = players.indexOf(currentUser);
        if (currentIndex != -1 && !lines.isEmpty()) {
            // Get previous player's index
            int previousIndex = (currentIndex - 1 + players.size()) % players.size();
            User previousPlayer = players.get(previousIndex);

            // Find last line written by previous player
            for (int i = lines.size() - 1; i >= 0; i--) {
                StoryLine line = lines.get(i);
                if (line.getAuthor().equals(previousPlayer)) {
                    visibleLine = line.getText();
                    break;
                }
            }
        }

        String currentPlayerNickname = (room.getCurrentPlayer() != null)
                ? room.getCurrentPlayer().getNickname()
                : "";

        int timeLeft = room.getTimeLeftForCurrentTurnInSeconds();

        String ownerNickname = (room.getOwner() != null) ? room.getOwner().getNickname() : "";

        String status = room.getStatus() != null ? room.getStatus().name() : "UNKNOWN";

        return new GameStateDto(visibleLine, currentPlayerNickname, timeLeft, ownerNickname, status);
    }




    @Transactional
    public GameRoomDto joinRoom(Long roomId, User user, JoinPrivateRoomRequest joinRequest) {
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        if (!room.isPublic()) {
            if (joinRequest == null || !room.getRoomCode().equals(joinRequest.roomCode())) {
                throw new NoAccessException("Room is private and requires a valid room code");
            }
        }

        if (!room.getStatus().equals(Status.WAITING_FOR_PLAYERS)) {
            throw new CurrentStatusException("Room is not accepting new players");
        }

        if (room.getCurrentPlayerCount() >= room.getMaxPlayers()) {
            throw new RoomFullException("Room is full");
        }

        if (room.getPlayers().contains(user)) {
            throw new DuplicateResourceException("User is already in the room");
        }

        room.getPlayers().add(user);
        room.setCurrentPlayerCount(room.getCurrentPlayerCount() + 1);


        GameRoomDto dto = convertToDTO(roomRepository.save(room));
        broadcastRoomList();
        return dto;

    }

    @Transactional
    public GameRoomDto leaveRoom(Long roomId, User user) {
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Game room with ID " + roomId + " not found"));

        if (!room.getPlayers().contains(user)) {
            throw new ResourceNotFoundException("User is not in the room with id: " + roomId);
        }


      //  if (room.getOwner().equals(user) && room.getCurrentPlayerCount() > 1) {
       //     throw new CurrentStatusException("Owner cannot leave the room if there are other players");
      //  }


        room.getPlayers().remove(user);
        room.setCurrentPlayerCount(room.getCurrentPlayerCount() - 1);


        if (room.getCurrentPlayer() != null && room.getCurrentPlayer().equals(user)) {
            room.setCurrentPlayer(null);
        }

        if (room.getCurrentPlayerCount() <= 0) {
            roomRepository.delete(room);
            broadcastRoomList();
            return null;
        }

        GameRoomDto dto = convertToDTO(roomRepository.save(room));
        broadcastRoomList();
        return dto;


    }

    @Transactional
    public void startGame(Long roomId, User user) {
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (!room.getOwner().getId().equals(user.getId())) {
            throw new NoAccessException("Only the owner can start the game");
        }

        if (room.getPlayers().size() < 2) {
            throw new CurrentStatusException("Not enough players to start the game");
        }

        room.setStatus(Status.IN_PROGRESS);
        room.setCurrentPlayer(room.getOwner());
        room.setTimeLeftForCurrentTurnInSeconds(room.getTimeLimitPerTurnInSeconds());
        roomRepository.save(room);

        broadcastGameState(room);

        broadcastRoomList();

    }

    @Transactional
    public void submitTurn(Long roomId, User user, SubmitTurnDto dto) {
        GameRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (!room.getCurrentPlayer().getId().equals(user.getId())) {
            throw new NoAccessException("Not your turn");
        }

        StoryLine line = new StoryLine();
        line.setText(dto.text());
        line.setAuthor(user);

        //line.setRoom(room);

        line.setStory(room.getStory());

        room.getStory().getStoryLines().add(line);
        storyLineRepository.save(line);

        List<User> players = new ArrayList<>(room.getPlayers());
        int currentIndex = players.indexOf(room.getCurrentPlayer());

        if (currentIndex == -1) {
            throw new IllegalStateException("Current player not found in the room");
        }

        int nextIndex = (currentIndex + 1) % players.size();
        boolean isNewRound = nextIndex == 0;

        if (isNewRound) {
            room.setTurnsPerPlayer(room.getTurnsPerPlayer() - 1);
            if (room.getTurnsPerPlayer() <= 0) {
                room.setStatus(Status.FINISHED);
                room.setCurrentPlayer(null);
            }
        }

        if (room.getStatus() != Status.FINISHED) {
            room.setCurrentPlayer(players.get(nextIndex));
            room.setTimeLeftForCurrentTurnInSeconds(room.getTimeLimitPerTurnInSeconds());
        }

        roomRepository.save(room);
        broadcastGameState(room);
    }


    void broadcastGameState(GameRoom room) {
        List<User> players = new ArrayList<>(room.getPlayers());
        List<StoryLine> lines = room.getStory().getStoryLines().stream()
                .sorted(Comparator.comparing(StoryLine::getId))
                .collect(Collectors.toList());

        String visibleLine = "";


        if (room.getCurrentPlayer() != null && !lines.isEmpty()) {
            int currentIndex = players.indexOf(room.getCurrentPlayer());
            int previousIndex = (currentIndex - 1 + players.size()) % players.size();
            User previousPlayer = players.get(previousIndex);

            for (int i = lines.size() - 1; i >= 0; i--) {
                StoryLine line = lines.get(i);
                if (line.getAuthor().equals(previousPlayer)) {
                    visibleLine = line.getText();
                    break;
                }
            }
        }

        GameStateDto state = new GameStateDto(
                visibleLine,
                room.getCurrentPlayer() != null ? room.getCurrentPlayer().getNickname() : "",
                room.getTimeLeftForCurrentTurnInSeconds(),
                room.getOwner() != null ? room.getOwner().getNickname() : "",
                room.getStatus() != null ? room.getStatus().name() : "UNKNOWN"
        );

        messagingTemplate.convertAndSend("/topic/room/" + room.getId() + "/state", state);
    }


    private void broadcastRoomList() {
        List<GameRoomDto> updatedRooms = roomRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        messagingTemplate.convertAndSend("/topic/rooms", updatedRooms);

    }

    private GameRoom convertToEntity(CreateRoomRequest request) {
        GameRoom room = new GameRoom();
        room.setTitle(request.title());
        room.setRoomCode(request.isPublic() ? null : request.roomCode());
        room.setPublic(request.isPublic());
        room.setMaxPlayers(request.maxPlayers());
        room.setTimeLimitPerTurnInSeconds(request.timeLimitPerTurnInSeconds());
        room.setTurnsPerPlayer(request.turnsPerPlayer());
        return room;
    }

    private GameRoomDto convertToDTO(GameRoom room) {
        return new GameRoomDto(
                room.getId(),
                room.getTitle(),
                room.isPublic(),
                room.getStatus(),
                room.getCurrentPlayerCount(),
                room.getMaxPlayers(),
                room.getOwner().getNickname(),
                room.getTimeLimitPerTurnInSeconds(),
                room.getTurnsPerPlayer()
        );
    }
}
