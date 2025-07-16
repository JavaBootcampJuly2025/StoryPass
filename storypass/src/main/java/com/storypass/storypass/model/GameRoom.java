package com.storypass.storypass.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "game_rooms")
@Getter
@Setter
@NoArgsConstructor
public class GameRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
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
}