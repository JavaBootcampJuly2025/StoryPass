package com.storypass.storypass.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Добавляем длину колонки в соответствии с планом (3-20 символов)
    @Column(unique = true, nullable = false, length = 20)
    private String login;

    // ВАЖНО: Пароль будет храниться в виде хэша, который длиннее 20 символов.
    // Поэтому ставим длину с запасом, например 100.
    @Column(nullable = false, length = 100)
    private String password;

    // Добавляем длину колонки в соответствии с планом (3-15 символов)
    @Column(unique = true, nullable = false, length = 15)
    private String nickname;

    public User() {
    }

    // getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}