package com.storypass.storypass.controller;

import com.storypass.storypass.dto.PlayerProfileDto;
import com.storypass.storypass.service.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public PlayerProfileDto getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return profileService.getProfile(userDetails.getUsername());
    }
}
