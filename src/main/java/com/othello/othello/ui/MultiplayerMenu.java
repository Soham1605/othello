package com.othello.othello.ui;

import javax.swing.*;
import java.awt.*;

public class MultiplayerMenu extends JFrame {

    public MultiplayerMenu() {
        setTitle("Multiplayer Menu");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1, 10, 10));

        JButton createButton = new JButton("Create Game");
        JButton joinButton = new JButton("Join Game");

        add(createButton);
        add(joinButton);

        createButton.addActionListener(e -> {
            try {
                Long gameId = ApiClient.createGame();
                JOptionPane.showMessageDialog(this, "Share this Game Code: " + gameId);
                new WaitingScreen(gameId); 
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to create game.");
            }
        });
        

        joinButton.addActionListener(e -> {
            new JoinGameScreen(); 
            dispose();
        });

        setVisible(true);
    }
}
