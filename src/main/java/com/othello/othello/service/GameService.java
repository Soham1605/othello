package com.othello.othello.service;

import com.othello.othello.model.GameResult;
import com.othello.othello.model.User;
import com.othello.othello.repository.GameResultRepository;
import com.othello.othello.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GameResultRepository resultRepo;

    public ResponseEntity<?> recordResult(String username, String result) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        switch (result.toUpperCase()) {
            case "WIN" -> user.setWins(user.getWins() + 1);
            case "LOSS" -> user.setLosses(user.getLosses() + 1);
            case "DRAW" -> user.setDraws(user.getDraws() + 1);
            default -> ResponseEntity.badRequest().body("Invalid result");
        }

        userRepo.save(user);
        resultRepo.save(new GameResult(null, username, result.toUpperCase()));
        return ResponseEntity.ok("Result recorded");
    }

    public ResponseEntity<?> getRecord(String username) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        return ResponseEntity.ok("W:" + user.getWins() + " L:" + user.getLosses() + " D:" + user.getDraws());
    }
}
