package com.storypass.storypass.model;


import jakarta.persistence.*;

@Entity
@Table(name = "story_lines")
public class StoryLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT") // Позволяет хранить длинный текст
    private String text;

    private int sequenceNumber;

    // many lines can be written by one player
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    // Many lines belong to one story
    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;


    public StoryLine() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public Story getStory() { return story; }
    public void setStory(Story story) { this.story = story; }
}