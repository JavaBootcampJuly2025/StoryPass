package com.storypass.storypass.dto;

public class StoryLineDto {
    private String text;
    private String authorNickname;

    // Constructors, Getters and Setters
    public StoryLineDto(String text, String authorNickname) {
        this.text = text;
        this.authorNickname = authorNickname;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
}