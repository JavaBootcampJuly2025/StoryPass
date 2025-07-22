package com.storypass.storypass.service;


import com.storypass.storypass.model.GameRoom;
import com.storypass.storypass.model.Status;
import com.storypass.storypass.repository.GameRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameTimerService {

    private final GameRoomRepository roomRepository;
    private final GameRoomService gameRoomService;

    @Autowired
    public GameTimerService(GameRoomRepository roomRepository, GameRoomService gameRoomService) {
        this.roomRepository = roomRepository;
        this.gameRoomService = gameRoomService;
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void decrementTimers() {
        List<GameRoom> inProgressRooms = roomRepository.findByStatus(Status.IN_PROGRESS);

        for (GameRoom room : inProgressRooms) {
            int timeLeft = room.getTimeLeftForCurrentTurnInSeconds();

            if (timeLeft > 0) {
                room.setTimeLeftForCurrentTurnInSeconds(timeLeft - 1);
                roomRepository.save(room);
                gameRoomService.broadcastGameState(room);
            } else {

                gameRoomService.skipTurn(room);
            }
        }
    }

}
