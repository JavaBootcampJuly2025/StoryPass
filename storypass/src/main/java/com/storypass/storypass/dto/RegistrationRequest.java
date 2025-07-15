// Пакет: com.storypass.storypass.dto
package com.storypass.storypass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationRequest {

    @NotBlank(message = "Login cannot be empty")
    @Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
    private String login;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
    private String password;

    @NotBlank(message = "Nickname cannot be empty")
    @Size(min = 3, max = 15, message = "Nickname must be between 3 and 15 characters")
    private String nickname;

    // Getters and Setters
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}