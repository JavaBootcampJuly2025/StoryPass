package com.storypass.storypass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubmitTurnDto {
    @NotBlank(message = "Your sentence cannot be empty")
    @Size(max = 250, message = "Sentence cannot be longer than 250 characters")
    private String text;

    // Getter and Setter
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}