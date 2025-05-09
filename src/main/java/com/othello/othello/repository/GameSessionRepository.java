package com.othello.othello.repository;

import com.othello.othello.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByGuestIsNullAndActiveTrue();
}
