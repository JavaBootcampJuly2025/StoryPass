package com.storypass.storypass.service;

import com.storypass.storypass.dto.AuthResponse;
import com.storypass.storypass.dto.LoginRequest;
import com.storypass.storypass.dto.RegistrationRequest;
import com.storypass.storypass.exception.DuplicateResourceException;
import com.storypass.storypass.exception.ResourceNotFoundException;
import com.storypass.storypass.model.User;
import com.storypass.storypass.repository.UserRepository;
import com.storypass.storypass.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        log.info("Attempting to register new user with login: {}", request.login());
        userRepository.findByLogin(request.login()).ifPresent(user -> {
            log.warn("Registration failed: login '{}' already exists.", request.login());
            throw new DuplicateResourceException("A user with the login '" + request.login() + "' already exists.");
        });

        userRepository.findByNickname(request.nickname()).ifPresent(user -> {
            log.warn("Registration failed: nickname '{}' already exists.", request.nickname());
            throw new DuplicateResourceException("A user with the nickname '" + request.nickname() + "' already exists.");
        });

        User user = new User();
        user.setLogin(request.login());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname());
        userRepository.save(user);

        log.info("User '{}' registered successfully with ID: {}", user.getLogin(), user.getId());

        String token = jwtService.generateToken(user.getLogin());
        return new AuthResponse(token, user.getNickname());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("User '{}' attempting to log in.", request.login());
        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> {
                    log.warn("Login failed for user '{}': user not found.", request.login());
                    return new ResourceNotFoundException("Incorrect login or password.");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed for user '{}': incorrect password.", request.login());
            throw new ResourceNotFoundException("Incorrect login or password.");
        }
        log.info("User '{}' logged in successfully.", user.getLogin());
        String token = jwtService.generateToken(user.getLogin());

        return new AuthResponse(token, user.getNickname());
    }
}