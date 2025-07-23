package com.storypass.storypass;

import com.storypass.storypass.repository.GameRoomRepository;
import com.storypass.storypass.service.GameRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class GameRoomServiceTest {

    GameRoomService gameRoomService;

    @BeforeEach
    void setUp() {
        var gameRoomRepository = Mockito.mock(GameRoomRepository.class);
        // TODO: Other two dependencies!

        gameRoomService = new GameRoomService(gameRoomRepository, null, null); // TODO: Inject all mocked dependencies!
    }

    @Test
    void test() {

    }
}
