package com.storypass.storypass;

import com.storypass.storypass.dto.PlayerProfileDto;
import com.storypass.storypass.model.User;
import com.storypass.storypass.repository.StoryRepository;
import com.storypass.storypass.repository.UserRepository;
import com.storypass.storypass.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTests {

    private final static String TEST_USER_LOGIN = "Wolf";
    private final static String TEST_USER_NICKNAME = "wolfy";
    private final static String TEST_USER_PASSWORD_ENCODED = "encodedPass123";

    @Mock UserRepository userRepository;
    @Mock StoryRepository storyRepository;

    @InjectMocks ProfileService profileService;

    @Test
    void shouldGetProfile() {
        User user = createTestUser();

        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        PlayerProfileDto profile = profileService.getProfile(user.getLogin());

        assertEquals(TEST_USER_NICKNAME, profile.nickname());
    }

    @Test
    void shouldThrowUsernameNotFoundException() {
        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> profileService.getProfile(TEST_USER_LOGIN));
    }

    // HELPER METHODS ==========
    private User createTestUser() {
        User user = new User();
        user.setLogin(TEST_USER_LOGIN);
        user.setPassword(TEST_USER_PASSWORD_ENCODED);
        user.setNickname(TEST_USER_NICKNAME);
        return user;
    }
}
