package com.storypass.storypass.dto;

import java.util.List;

public class FullStoryDto {
    private String title;
    private List<StoryLineDto> lines;

    // Constructors, Getters and Setters
    public FullStoryDto(String title, List<StoryLineDto> lines) {
        this.title = title;
        this.lines = lines;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<StoryLineDto> getLines() { return lines; }
    public void setLines(List<StoryLineDto> lines) { this.lines = lines; }
}