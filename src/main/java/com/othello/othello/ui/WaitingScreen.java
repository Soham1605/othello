package com.othello.othello.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class WaitingScreen extends JFrame {
    private Timer pollingTimer;
    private Timer animationTimer;
    private Long gameId;
    private JLabel label;
    private int dotCount = 0;

    public WaitingScreen(Long gameId) {
        this.gameId = gameId;

        setTitle("Waiting for Opponent...");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        label = new JLabel("Waiting for opponent", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);

        startPolling();
        startAnimation();

        setVisible(true);
    }

    private void startPolling() {
        pollingTimer = new Timer();
        pollingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String state = ApiClient.getGameState(gameId);
                    if (state.contains("\"guest\":\"")) {  
                        pollingTimer.cancel();
                        animationTimer.cancel();
                        SwingUtilities.invokeLater(() -> {
                            new OthelloBoard(true, gameId);
                            dispose();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3000); 
    }

    private void startAnimation() {
        animationTimer = new Timer();
        animationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                dotCount = (dotCount + 1) % 4; 
                String dots = ".".repeat(dotCount);
                SwingUtilities.invokeLater(() -> label.setText("Waiting for opponent" + dots));
            }
        }, 0, 500); 
    }
}
