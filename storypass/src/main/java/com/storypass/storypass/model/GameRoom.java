package com.storypass.storypass.model;


import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "game_rooms")
public class GameRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // ИЗМЕНЕНИЕ: Убрана аннотация @Column(unique = true), код больше не уникальный
    private String roomCode;

    private boolean isPublic;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int maxPlayers;
    private int currentPlayerCount;
    private int timeLimitPerTurnInSeconds;
    private int turnsPerPlayer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "story_id", referencedColumnName = "id")
    private Story story;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "room_players",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> players = new HashSet<>();

    public GameRoom() {
    }

    // Getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public int getCurrentPlayerCount() { return currentPlayerCount; }
    public void setCurrentPlayerCount(int currentPlayerCount) { this.currentPlayerCount = currentPlayerCount; }
    public int getTimeLimitPerTurnInSeconds() { return timeLimitPerTurnInSeconds; }
    public void setTimeLimitPerTurnInSeconds(int timeLimitPerTurnInSeconds) { this.timeLimitPerTurnInSeconds = timeLimitPerTurnInSeconds; }
    public int getTurnsPerPlayer() { return turnsPerPlayer; }
    public void setTurnsPerPlayer(int turnsPerPlayer) { this.turnsPerPlayer = turnsPerPlayer; }
    public Story getStory() { return story; }
    public void setStory(Story story) { this.story = story; }
    public Set<User> getPlayers() { return players; }
    public void setPlayers(Set<User> players) { this.players = players; }
}