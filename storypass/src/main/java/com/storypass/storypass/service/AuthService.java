package com.storypass.storypass.service;

import com.storypass.storypass.model.User;
import com.storypass.storypass.repository.UserRepository;
import com.storypass.storypass.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(String login, String password, String nickname) {
        Optional<User> existingByLogin = userRepository.findByLogin(login);
        if (existingByLogin.isPresent()) {
            throw new RuntimeException("Login already taken");
        }

        Optional<User> existingByNickname = userRepository.findByNickname(nickname);
        if (existingByNickname.isPresent()) {
            throw new RuntimeException("Nickname already taken");
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);

        userRepository.save(user);
    }

    public String login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return jwtService.generateToken(login);
        }
        throw new RuntimeException("Invalid credentials");
    }
}
