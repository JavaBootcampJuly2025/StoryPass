package com.storypass.storypass;

import com.storypass.storypass.dto.AuthResponse;
import com.storypass.storypass.dto.LoginRequest;
import com.storypass.storypass.repository.UserRepository;
import com.storypass.storypass.security.JwtService;
import com.storypass.storypass.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class AuthServiceTest {

    // One way to do it:
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    /**
     * Another way:
     * userRepository = Mockito.mock(UserRepository.class);
     * authService = new AuthService(userRepository, and-other-dependencies-go-here);
     * This is typically done in a separate method with @BeforeEach annotation
     */

    //yo
    @InjectMocks // Of course it does not work! You need to inject the actual mocks!
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