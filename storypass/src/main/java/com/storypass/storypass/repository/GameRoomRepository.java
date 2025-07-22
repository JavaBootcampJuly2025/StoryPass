package com.storypass.storypass.repository;

import com.storypass.storypass.model.GameRoom;
import com.storypass.storypass.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    Optional<GameRoom> findByRoomCode(String roomCode);
    List<GameRoom> findByStatus(Status status);
}
