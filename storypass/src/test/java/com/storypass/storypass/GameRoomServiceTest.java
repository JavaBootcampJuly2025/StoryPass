package com.storypass.storypass;

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
import com.storypass.storypass.service.GameRoomService;
import com.storypass.storypass.service.StoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameRoomServiceTest {

    private final static String TEST_USER_LOGIN = "Wolf";
    private final static String TEST_USER_NICKNAME = "wolfy";
    private final static String TEST_USER_PASSWORD_ENCODED = "encodedPass123";
    private final static String TEST_ROOM_CODE = "1234";


    @Mock GameRoomRepository roomRepository;
    @Mock StoryService storyService;
    @Mock SimpMessagingTemplate messagingTemplate;

    @InjectMocks GameRoomService gameRoomService;

    // CREATE OR DELETE ROOMS TESTS ==============================
    @org.junit.jupiter.api.Nested
    class CreateOrDeleteRooms {

        @Test
        void shouldCreateNewPublicRoom() {
            CreateRoomRequest request = createPublicRoomRequest();

            GameRoomDto resp = gameRoomService.createNewRoom(request, createTestUser());

            assertEquals("public test room", resp.title());
            assertEquals(TEST_USER_NICKNAME, resp.ownerNickname());
            assertTrue(resp.isPublic());
        }

        @Test
        void shouldCreateNewPrivateRoom() {
            CreateRoomRequest request = createPrivateRoomRequest();

            GameRoomDto resp = gameRoomService.createNewRoom(request, createTestUser());

            assertEquals("private test room", resp.title());
            assertEquals(TEST_USER_NICKNAME, resp.ownerNickname());
            assertFalse(resp.isPublic());
        }

        @Test
        void shouldNotFindRoomToDelete() {
            assertThrows(ResourceNotFoundException.class, () -> gameRoomService.deleteRoomById(1L, createTestUser()));
        }

        @Test
        void shouldNotHaveAccessToRoomToDelete() {
            GameRoom room = createPublicRoom();
            User nonOwner = createTestUser();

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertThrows(NoAccessException.class, () -> gameRoomService.deleteRoomById(1L, nonOwner));
        }

    }

    // GET ROOMS TESTS =========================
    @org.junit.jupiter.api.Nested
    class GettingRooms {
        @Test
        void shouldReturnEmptyList() {
            when(roomRepository.findAll()).thenReturn(java.util.Collections.emptyList());
            assertEquals(0, gameRoomService.getAllRooms().size());
        }

        @Test
        void shouldReturnRoomList() {
            GameRoom room = createPublicRoom();

            when(roomRepository.findAll()).thenReturn(java.util.List.of(room));

            assertEquals(1, gameRoomService.getAllRooms().size());
            assertEquals("public room", gameRoomService.getAllRooms().get(0).title());
        }

        @Test
        void shouldReturnRoomById() {
            GameRoom room = createPublicRoom();

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertEquals("public room", gameRoomService.getRoomById(1L).title());
        }

        @Test
        void shouldThrowRoomNotFound() {
            when(roomRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> gameRoomService.getRoomById(1L));
        }
    }

    // JOIN ROOM TESTS =============================
    @org.junit.jupiter.api.Nested
    class JoinRoom {

        @Test
        void shouldJoinPublicRoom() {
            GameRoom room = createPublicRoom();
            room.setId(1L);

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomRepository.save(room)).thenReturn(room);

            GameRoomDto roomDto = gameRoomService.joinRoom(1L, createTestUser(), null);

            assertEquals("public room", roomDto.title());
            assertEquals(2, roomDto.currentPlayerCount());
            assertTrue(roomDto.isPublic());
        }

        @Test
        void  shouldJoinPrivateRoom() {
            GameRoom room = createPrivateRoom();
            JoinPrivateRoomRequest joinRequest = new JoinPrivateRoomRequest(TEST_ROOM_CODE);

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomRepository.save(room)).thenReturn(room);

            GameRoomDto roomDto = gameRoomService.joinRoom(1L, createTestUser(), joinRequest);

            assertEquals("private room", roomDto.title());
            assertEquals(2, roomDto.currentPlayerCount());
            assertFalse(roomDto.isPublic());
        }

        @Test
        void shouldThrowNoAccessExceptionBecauseNoCodeProvided() {
            GameRoom room = createPrivateRoom();

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertThrows(NoAccessException.class, () -> gameRoomService.joinRoom(1L, createTestUser(), null));
        }

        @Test
        void shouldThrowNoAccessExceptionBecauseWrongCodeProvided() {
            GameRoom room = createPrivateRoom();
            JoinPrivateRoomRequest joinRequest = new JoinPrivateRoomRequest("wrong code");

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertThrows(NoAccessException.class, () -> gameRoomService.joinRoom(1L, createTestUser(), joinRequest));
        }

        @Test
        void shouldThrowRoomNotFound() {
            assertThrows(ResourceNotFoundException.class, () -> gameRoomService.joinRoom(1L, createTestUser(), null));
        }

        @Test
        void shouldThrowCurrentStatusException() {
            GameRoom room = createPublicRoom();
            room.setStatus(Status.IN_PROGRESS);

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertThrows(CurrentStatusException.class, () -> gameRoomService.joinRoom(1L, createTestUser(), null));
        }

        @Test
        void shouldThrowRoomFullException() {
            GameRoom room = createPublicRoom();
            room.setMaxPlayers(1);

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertThrows(RoomFullException.class, () -> gameRoomService.joinRoom(1L, createTestUser(), null));
        }

        @Test
        void shouldThrowDuplicateResourceException() {
            GameRoom room = createPublicRoom();
            User user = createTestUser();

            room.getPlayers().add(user);

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertThrows(DuplicateResourceException.class, () -> gameRoomService.joinRoom(1L, user, null));
        }

    }

    // LEAVE ROOM TESTS =======================
    @org.junit.jupiter.api.Nested
    class LeaveRoom {

        @Test
        void shouldLeaveRoom() {
            GameRoom room = createPublicRoom();
            User user2 = createTestUser();
            room.getPlayers().add(user2);
            room.setCurrentPlayerCount(2);

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomRepository.save(room)).thenReturn(room);

            GameRoomDto roomDto = gameRoomService.leaveRoom(1L, user2);

            assertEquals(1, roomDto.currentPlayerCount());
        }

        @Test
        void shouldThrowResourceNotFoundExceptionBecauseRoomNotFound() {
            assertThrows(ResourceNotFoundException.class, () -> gameRoomService.leaveRoom(1L, createTestUser()));
        }

        @Test
        void shouldThrowResourceNotFoundExceptionBecauseUserNotFound() {
            GameRoom room = createPublicRoom();

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertThrows(ResourceNotFoundException.class, () -> gameRoomService.leaveRoom(1L, createTestUser()));
        }

        @Test
        void shouldThrowCurrentStatusExceptionBecauseOwnerCannotLeaveWhenOthersInRoom() {
            GameRoom room = createPublicRoom();
            User owner = room.getOwner();
            User user = createTestUser();

            room.getPlayers().add(user);
            room.setCurrentPlayerCount(2);

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertThrows(CurrentStatusException.class, () -> gameRoomService.leaveRoom(1L, owner));
        }

    }

    // UPDATE ROOM TESTS ================
    @org.junit.jupiter.api.Nested
    class UpdateRoom {

        @Test
        void shouldUpdateRoom() {
            GameRoom room = createPublicRoom();
            User owner = room.getOwner();
            CreateRoomRequest request = createPublicRoomRequest("updated room");

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomRepository.save(room)).thenReturn(room);

            GameRoomDto roomDto = gameRoomService.updateRoomById(1L, owner, request);

            assertEquals("updated room", roomDto.title());
        }

        @Test
        void shouldResourceNotFoundExceptionBecauseRoomNotFound() {
            assertThrows(ResourceNotFoundException.class, () -> gameRoomService.updateRoomById(1L, createTestUser(), createPublicRoomRequest()));
        }

        @Test
        void shouldThrowResourceNotFoundExceptionBecauseUserNotOwner() {
            GameRoom room = createPublicRoom();

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertThrows(NoAccessException.class, () -> gameRoomService.updateRoomById(1L, createTestUser(), createPublicRoomRequest()));
        }

    }

    @org.junit.jupiter.api.Nested
    class GetGameState {

        @Test
        void shouldReturnGameState() {
            GameRoom room = createPublicRoom();

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            GameStateDto gameState = gameRoomService.getGameState(1L, createTestUser());

            assertEquals(TEST_USER_NICKNAME, gameState.getOwnerNickname());
        }

        @Test
        void shouldThrowResourceNotFoundException() {
            assertThrows(ResourceNotFoundException.class, () -> gameRoomService.getGameState(1L, createTestUser()));
        }

    }

    // HELPER METHODS ========
    CreateRoomRequest createPublicRoomRequest(String... title) {
        return new CreateRoomRequest(
                title.length > 0 ? title[0] : "public test room",
                true,
                null,
                10,
                60,
                1);
    }

    CreateRoomRequest createPrivateRoomRequest() {
        return new CreateRoomRequest(
                "private test room",
                false,
                TEST_ROOM_CODE,
                10,
                60,
                1);
    }

    private User createTestUser() {
        User user = new User();
        user.setLogin(TEST_USER_LOGIN);
        user.setPassword(TEST_USER_PASSWORD_ENCODED);
        user.setNickname(TEST_USER_NICKNAME);
        return user;
    }

    private GameRoom createPublicRoom() {
        User owner = createTestUser();

        GameRoom room = new GameRoom();
        room.setId(1L);
        room.setTitle("public room");
        room.setPublic(true);
        room.setOwner(owner);
        room.getPlayers().add(owner);
        room.setStatus(Status.WAITING_FOR_PLAYERS);
        room.setMaxPlayers(10);
        room.setCurrentPlayerCount(1);
        room.setStory(new Story());
        return room;
    }

    private GameRoom createPrivateRoom() {
        User owner = createTestUser();

        GameRoom room = new GameRoom();
        room.setId(1L);
        room.setTitle("private room");
        room.setRoomCode(TEST_ROOM_CODE);
        room.setOwner(owner);
        room.getPlayers().add(owner);
        room.setStatus(Status.WAITING_FOR_PLAYERS);
        room.setMaxPlayers(10);
        room.setCurrentPlayerCount(1);
        room.setStory(new Story());
        return room;
    }
}
