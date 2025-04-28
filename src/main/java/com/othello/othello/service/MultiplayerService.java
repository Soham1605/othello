package com.othello.othello.service;

import com.othello.othello.model.GameSession;
import com.othello.othello.repository.GameSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MultiplayerService {

    @Autowired
    private GameSessionRepository sessionRepo;

    public ResponseEntity<?> createGame(String host) {
        GameSession session = new GameSession();
        session.setHost(host);
        session.setBoard(
            "...................................BW......WB..................................."
        ); 
        session.setCurrentTurn("B");
        session.setCurrentPlayer(host);
        session.setActive(true);
        sessionRepo.save(session);
        return ResponseEntity.ok(session);
    }

    public ResponseEntity<?> joinGame(Long gameId, String guest) {
        Optional<GameSession> optionalSession = sessionRepo.findById(gameId);
        if (optionalSession.isPresent()) {
            GameSession session = optionalSession.get();
            if (session.getGuest() != null) {
                return ResponseEntity.badRequest().body("Game already has a guest");
            }
            session.setGuest(guest);
            sessionRepo.save(session);
            return ResponseEntity.ok(session);
        } else {
            return ResponseEntity.badRequest().body("Game not found");
        }
    }

    public ResponseEntity<?> listOpenGames() {
        List<GameSession> openGames = sessionRepo.findByGuestIsNullAndActiveTrue();
        return ResponseEntity.ok(openGames);
    }

    public ResponseEntity<?> getGameState(Long gameId) {
        Optional<GameSession> session = sessionRepo.findById(gameId);
        return session.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Game not found"));
    }

    public ResponseEntity<?> updateGameState(GameSession updatedSession) {
        Optional<GameSession> sessionOpt = sessionRepo.findById(updatedSession.getId());
        if (sessionOpt.isPresent()) {
            GameSession session = sessionOpt.get();
            session.setBoard(updatedSession.getBoard());
            session.setCurrentTurn(updatedSession.getCurrentTurn());
            session.setActive(updatedSession.isActive());
            sessionRepo.save(session);
            return ResponseEntity.ok("Game updated");
        } else {
            return ResponseEntity.badRequest().body("Game not found");
        }
    }
}
