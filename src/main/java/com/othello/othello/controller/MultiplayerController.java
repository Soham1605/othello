package com.othello.othello.controller;

import com.othello.othello.model.GameSession;
import com.othello.othello.service.MultiplayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/multiplayer")
public class MultiplayerController {

    @Autowired
    private MultiplayerService multiplayerService;

    @PostMapping("/create")
    public ResponseEntity<?> createGame() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String hostUsername = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return multiplayerService.createGame(hostUsername);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinGame(@RequestParam Long gameId, @RequestParam String guest) {
        return multiplayerService.joinGame(gameId, guest);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listOpenGames() {
        return multiplayerService.listOpenGames();
    }

    @GetMapping("/state")
    public ResponseEntity<?> getGameState(@RequestParam Long gameId) {
        return multiplayerService.getGameState(gameId);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateGameState(@RequestBody GameSession session) {
        return multiplayerService.updateGameState(session);
    }
}
