package com.storypass.storypass;

import com.storypass.storypass.dto.AuthResponse;
import com.storypass.storypass.dto.LoginRequest;
import com.storypass.storypass.dto.RegistrationRequest;
import com.storypass.storypass.exception.DuplicateResourceException;
import com.storypass.storypass.exception.ResourceNotFoundException;
import com.storypass.storypass.model.User;
import com.storypass.storypass.repository.UserRepository;
import com.storypass.storypass.security.JwtService;
import com.storypass.storypass.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private final static String TEST_USER_LOGIN = "Wolf";
    private final static String TEST_USER_NICKNAME = "wolfy";
    private final static String TEST_USER_PASSWORD = "pass123";
    private final static String TEST_USER_PASSWORD_ENCODED = "encodedPass123";
    private final static String TEST_USER_TOKEN = "jwtToken";

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthService authService;

    // REGISTERING TESTS ==============
    @Test
    void shouldRegister() {
        RegistrationRequest request = new RegistrationRequest(TEST_USER_LOGIN, TEST_USER_PASSWORD, TEST_USER_NICKNAME);

        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.empty());
        when(userRepository.findByNickname(TEST_USER_NICKNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn(TEST_USER_PASSWORD_ENCODED);
        when(jwtService.generateToken(TEST_USER_LOGIN)).thenReturn(TEST_USER_TOKEN);

        AuthResponse resp = authService.register(request);
        assertEquals(TEST_USER_TOKEN, resp.token());
        assertEquals(TEST_USER_NICKNAME, resp.nickname());
    }

    @Test
    void shouldThrowUserAlreadyRegistered() {
        RegistrationRequest request = new RegistrationRequest(TEST_USER_LOGIN, TEST_USER_PASSWORD, TEST_USER_NICKNAME);

        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));

    }

    @Test
    void shouldThrowNicknameAlreadyRegistered() {
        RegistrationRequest request = new RegistrationRequest(TEST_USER_LOGIN, TEST_USER_PASSWORD, TEST_USER_NICKNAME);

        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.empty());
        when(userRepository.findByNickname(TEST_USER_NICKNAME)).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    // LOGIN TESTS ==================
    @Test
    void shouldLogin() {

        LoginRequest loginR = new LoginRequest(TEST_USER_LOGIN, TEST_USER_PASSWORD);

        User user = createTestUser();

        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_USER_PASSWORD, TEST_USER_PASSWORD_ENCODED)).thenReturn(true);
        when(jwtService.generateToken(TEST_USER_LOGIN)).thenReturn(TEST_USER_TOKEN);

        AuthResponse resp = authService.login(loginR);
        assertEquals(TEST_USER_TOKEN, resp.token());
        assertEquals(TEST_USER_NICKNAME, resp.nickname());
    }

    @Test
    void shouldThrowUserNotFound() {
        LoginRequest request = new LoginRequest(TEST_USER_LOGIN, TEST_USER_PASSWORD);

        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void shouldThrowIncorrectPassword() {
        LoginRequest request = new LoginRequest(TEST_USER_LOGIN, TEST_USER_PASSWORD);

        User user = createTestUser();

        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_USER_PASSWORD, TEST_USER_PASSWORD_ENCODED)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
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