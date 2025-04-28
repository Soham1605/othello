package com.othello.othello.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.io.IOException;

public class ApiClient {
    public static String token = null;
    public static String username = null;

    public static String pullGameState(Long gameId) throws Exception {
        URL url = new URL("http://localhost:8081/api/multiplayer/state?gameId=" + gameId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        Scanner scanner = new Scanner(conn.getInputStream());
        String response = scanner.useDelimiter("\\A").next();
        scanner.close();
        return response;
    }

    public static void pushGameState(Long gameId, String board, String currentTurn, String guest) throws Exception {
        URL url = new URL("http://localhost:8081/api/multiplayer/update");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Content-Type", "application/json");

        String payload = String.format(
            "{\"id\":%d,\"board\":\"%s\",\"currentTurn\":\"%s\",\"active\":true}",
            gameId, board, currentTurn
        );

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
        }

        conn.getInputStream().close();
    }

    public static void deactivateGame(Long gameId) throws Exception {
        URL url = new URL("http://localhost:8081/api/multiplayer/update");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Content-Type", "application/json");

        String payload = String.format(
            "{\"id\":%d,\"active\":false}",
            gameId
        );

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
        }

        conn.getInputStream().close();
    }

    public static void recordResult(String username, String result) {
        try {
            URL url = new URL("http://localhost:8081/api/game/result?username=" + username + "&result=" + result);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.getInputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String fetchRecord() throws Exception {
        URL url = new URL("http://localhost:8081/api/game/record?username=" + username);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        Scanner scanner = new Scanner(conn.getInputStream());
        String response = scanner.useDelimiter("\\A").next();
        scanner.close();
        return response;
    }

    public static boolean login(String usernameInput, String passwordInput) {
        try {
            URL url = new URL("http://localhost:8081/api/auth/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
    
            String payload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", usernameInput, passwordInput);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
            }
    
            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream());
                String tokenResponse = scanner.useDelimiter("\\A").next();
                scanner.close();
                token = tokenResponse.replace("\"", ""); 
                System.out.println("[DEBUG] Token after login: " + token);
                username = usernameInput;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public static boolean register(String username, String password) {
        try {
            URL url = new URL("http://localhost:8081/api/auth/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
    
            String payload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
            }
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static Long createGame() throws Exception {
        System.out.println("[DEBUG] Authorization header: Bearer " + token);
        URL url = new URL("http://localhost:8081/api/multiplayer/create");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + token); // <-- CRITICAL
        conn.setDoOutput(true);

        if (conn.getResponseCode() != 200) {
            throw new IOException("Failed to create game: HTTP " + conn.getResponseCode());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = in.readLine();
        in.close();

        // Parse Game ID from response
        if (response.contains("\"id\":")) {
            String idPart = response.split("\"id\":")[1].split(",")[0];
            return Long.parseLong(idPart.trim());
        } else {
            throw new IOException("Invalid server response: " + response);
        }
    }

    
    
    
    public static boolean joinGame(Long gameId) {
        try {
            URL url = new URL("http://localhost:8081/api/multiplayer/join?gameId=" + gameId + "&guest=" + username);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
    
            conn.getInputStream().close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String getGameState(Long gameId) throws Exception {
        URL url = new URL("http://localhost:8081/api/multiplayer/state?gameId=" + gameId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
    
        Scanner scanner = new Scanner(conn.getInputStream());
        String response = scanner.useDelimiter("\\A").next();
        scanner.close();
        return response;
    }
    
}
