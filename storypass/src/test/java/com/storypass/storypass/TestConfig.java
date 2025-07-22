package com.storypass.storypass;

import com.storypass.storypass.repository.UserRepository;
import com.storypass.storypass.security.JwtAuthenticationFilter;
import com.storypass.storypass.security.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableWebSecurity
public class TestConfig {

    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }
}

