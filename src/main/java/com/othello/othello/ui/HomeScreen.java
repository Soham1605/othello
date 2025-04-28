package com.othello.othello.ui;

import javax.swing.*;
import java.awt.*;

public class HomeScreen extends JFrame {

    public HomeScreen() {
        setTitle("Othello Home");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        JLabel helloLabel = new JLabel("Hello, " + ApiClient.username + "!", SwingConstants.CENTER);
        helloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(helloLabel, BorderLayout.NORTH);

        JLabel recordLabel = new JLabel("", SwingConstants.CENTER);
        recordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(recordLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton playComputerButton = new JButton("Play vs Computer");
        JButton playOnlineButton = new JButton("Play Online");
        buttonPanel.add(playComputerButton);
        buttonPanel.add(playOnlineButton);
        add(buttonPanel, BorderLayout.SOUTH);

        playComputerButton.addActionListener(e -> {
            dispose();
            new SinglePlayerSetup();
        });

        playOnlineButton.addActionListener(e -> {
            dispose();
            new MultiplayerMenu();
        });

        setVisible(true);

        // 🛠 Refresh WLD after the GUI is visible
        SwingUtilities.invokeLater(() -> {
            try {
                String record = ApiClient.fetchRecord();
                recordLabel.setText(record);
            } catch (Exception e) {
                recordLabel.setText("Failed to load record.");
            }
        });
    }
}
