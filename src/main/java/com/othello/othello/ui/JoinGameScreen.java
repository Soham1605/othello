package com.othello.othello.ui;

import javax.swing.*;
import java.awt.*;

public class JoinGameScreen extends JFrame {
    public JoinGameScreen() {
        setTitle("Join Game");
        setSize(300, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel label = new JLabel("Enter Game Code:", SwingConstants.CENTER);
        JTextField codeField = new JTextField();
        JButton joinButton = new JButton("Join");

        add(label, BorderLayout.NORTH);
        add(codeField, BorderLayout.CENTER);
        add(joinButton, BorderLayout.SOUTH);

        joinButton.addActionListener(e -> {
            String input = codeField.getText().trim();
            if (!input.isEmpty()) {
                try {
                    Long gameId = Long.parseLong(input);
                    boolean success = ApiClient.joinGame(gameId);
                    if (success) {
                        new OthelloBoard(true, gameId);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to join game. Maybe wrong code?");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid Game ID.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a Game Code.");
            }
        });

        setVisible(true);
    }
}
