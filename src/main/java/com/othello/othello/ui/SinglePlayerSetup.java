package com.othello.othello.ui;

import javax.swing.*;

public class SinglePlayerSetup extends JFrame {

    public SinglePlayerSetup() {
        String[] options = {"Basic", "Intermediate", "Advanced"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Select AI Difficulty:",
                "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice >= 0) {
            new OthelloBoard(false, options[choice]);
            dispose();
        } else {
            new HomeScreen();
            dispose();
        }
    }
}
