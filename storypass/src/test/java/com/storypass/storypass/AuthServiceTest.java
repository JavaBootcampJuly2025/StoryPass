package com.storypass.storypass;

import com.storypass.storypass.dto.AuthResponse;
import com.storypass.storypass.dto.LoginRequest;
import com.storypass.storypass.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {
    
    private AuthService authService;

    LoginRequest loginR;

    @BeforeEach
    void setUp() {
        LoginRequest loginR = new LoginRequest("Wolf", "trash_talker");
    }

    @Test
    void shouldLogin() {
        AuthResponse resp = authService.login(loginR);
        assertEquals(AuthResponse.class, resp.getClass());
    }

}