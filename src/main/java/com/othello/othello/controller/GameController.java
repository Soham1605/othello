package com.othello.othello.controller;

import com.othello.othello.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/result")
    public ResponseEntity<?> recordResult(@RequestParam String username, @RequestParam String result) {
        return gameService.recordResult(username, result);
    }

    @GetMapping("/record")
    public ResponseEntity<?> getRecord(@RequestParam String username) {
        return gameService.getRecord(username);
    }
}
