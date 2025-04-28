package com.othello.othello.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String host;
    private String guest;

    private String board;        // 64-char string representing board state
    private String currentTurn;  // "B" or "W"

    @Column(name = "current_player", nullable = false)
    private String currentPlayer;   // 🆕 Added to match database table!

    private boolean active = true;
}
