package com.storypass.storypass.service;

import com.storypass.storypass.model.User;
import com.storypass.storypass.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public boolean login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        return userOpt.isPresent() &&
                passwordEncoder.matches(password, userOpt.get().getPassword());
    }
}
