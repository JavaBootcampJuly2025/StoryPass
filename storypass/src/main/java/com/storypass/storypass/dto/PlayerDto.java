package com.storypass.storypass.dto;

public class PlayerDto {
    private Long id;
    private String nickname;

    public PlayerDto() {}

    public PlayerDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
