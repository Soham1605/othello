package com.othello.othello.ui;

import javax.swing.*;
import java.awt.*;

public class LoginRegisterScreen extends JFrame {

    public LoginRegisterScreen() {
        setTitle("Othello Login/Register");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Welcome to Othello!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        formPanel.add(new JLabel("Username:", SwingConstants.CENTER));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:", SwingConstants.CENTER));
        formPanel.add(passwordField);
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            try {
                boolean success = ApiClient.login(usernameField.getText(), new String(passwordField.getPassword()));
                if (success) {
                    new HomeScreen();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Login failed.");
            }
        });

        registerButton.addActionListener(e -> {
            try {
                boolean success = ApiClient.register(usernameField.getText(), new String(passwordField.getPassword()));
                if (success) {
                    JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
                } else {
                    JOptionPane.showMessageDialog(this, "Username already exists.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Registration failed.");
            }
        });

        setVisible(true);
    }
}
