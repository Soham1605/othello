package com.othello.othello.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class OthelloBoard extends JFrame {
    private final int SIZE = 8;
    private final char[][] board = new char[SIZE][SIZE];
    private boolean isHost;
    private boolean isMultiplayer;
    private Long gameId;
    private char myColor;
    private String currentTurn = "B";
    private JLabel blackLabel;
    private JLabel whiteLabel;
    private JLabel turnLabel;
    private Timer syncTimer;
    private boolean aiGame = false;
    private String difficulty = "";
    private TimerTask aiMoveTask;
    private Timer aiTimer = new Timer();
    private boolean gameEnded = false;

    public OthelloBoard(boolean multiplayer, Long gameId) {
        this.isMultiplayer = multiplayer;
        this.gameId = gameId;
        init();
        if (multiplayer) {
            determineRole();
            startMultiplayerSync();
        }
    }

    public OthelloBoard(boolean multiplayer, String difficulty) {
        this.isMultiplayer = false;
        this.difficulty = difficulty;
        this.aiGame = true;
        init();
        myColor = 'B';
    }

    private void init() {
        setTitle("Othello Game");
        setSize(700, 850);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeBoard();

        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        infoPanel.setBackground(Color.DARK_GRAY);

        blackLabel = new JLabel("", SwingConstants.CENTER);
        blackLabel.setForeground(Color.WHITE);
        whiteLabel = new JLabel("", SwingConstants.CENTER);
        whiteLabel.setForeground(Color.WHITE);

        infoPanel.add(blackLabel);
        infoPanel.add(whiteLabel);

        add(infoPanel, BorderLayout.NORTH);
        add(new BoardPanel(), BorderLayout.CENTER);

        turnLabel = new JLabel("", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(turnLabel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeGame();
            }
        });

        updateLabels();
        setVisible(true);
    }

    private void initializeBoard() {
        for (char[] row : board) Arrays.fill(row, '.');
        board[3][3] = 'W';
        board[3][4] = 'B';
        board[4][3] = 'B';
        board[4][4] = 'W';
        currentTurn = "B";
    }

    private void determineRole() {
        try {
            String state = ApiClient.pullGameState(gameId);
            if (state.contains(ApiClient.username)) {
                isHost = state.contains("\"host\":\"" + ApiClient.username + "\"");
            }
            myColor = isHost ? 'B' : 'W';
        } catch (Exception e) {
            e.printStackTrace();
            isHost = true;
            myColor = 'B';
        }
    }

    private void updateLabels() {
        if (isMultiplayer) {
            blackLabel.setText((myColor == 'B' ? ApiClient.username : "Opponent") + " (Black): " + countPieces('B'));
            whiteLabel.setText((myColor == 'W' ? ApiClient.username : "Opponent") + " (White): " + countPieces('W'));
        } else {
            blackLabel.setText("Black: " + countPieces('B'));
            whiteLabel.setText("White: " + countPieces('W'));
        }
        turnLabel.setText(isMyTurn() ? "Your turn" : "Waiting...");
    }

    private void startMultiplayerSync() {
        syncTimer = new Timer();
        syncTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String state = ApiClient.pullGameState(gameId);
                    parseServerState(state);
                    SwingUtilities.invokeLater(() -> {
                        updateLabels();
                        repaint();
                        checkForStuckPlayer();
                        checkEnd();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    private void parseServerState(String stateJson) throws Exception {
        if (stateJson == null) return;
        String boardString = stateJson.split("\"board\":\"")[1].split("\"")[0];
        String turn = stateJson.split("\"currentTurn\":\"")[1].split("\"")[0];
        for (int i = 0; i < SIZE * SIZE; i++) {
            board[i / SIZE][i % SIZE] = boardString.charAt(i);
        }
        currentTurn = turn;
    }

    private boolean isMyTurn() {
        return currentTurn.charAt(0) == myColor;
    }

    private int countPieces(char color) {
        int count = 0;
        for (char[] row : board) {
            for (char c : row) {
                if (c == color) count++;
            }
        }
        return count;
    }

    private List<Point> validMoves(char player) {
        List<Point> moves = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (isValidMove(r, c, player)) moves.add(new Point(r, c));
            }
        }
        return moves;
    }

    private boolean isValidMove(int row, int col, char player) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || board[row][col] != '.') return false;
        char opponent = (player == 'B') ? 'W' : 'B';
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int d = 0; d < 8; d++) {
            int r = row + dr[d], c = col + dc[d];
            boolean foundOpponent = false;
            while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == opponent) {
                foundOpponent = true;
                r += dr[d];
                c += dc[d];
            }
            if (foundOpponent && r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == player) return true;
        }
        return false;
    }

    private boolean makeMove(int row, int col, char player) {
        if (!isValidMove(row, col, player)) return false;
        board[row][col] = player;
        flipPieces(row, col, player);
        return true;
    }

    private void flipPieces(int row, int col, char player) {
        char opponent = (player == 'B') ? 'W' : 'B';
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int d = 0; d < 8; d++) {
            int r = row + dr[d], c = col + dc[d];
            List<Point> toFlip = new ArrayList<>();
            while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == opponent) {
                toFlip.add(new Point(r, c));
                r += dr[d];
                c += dc[d];
            }
            if (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == player) {
                for (Point p : toFlip) board[p.x][p.y] = player;
            }
        }
    }

    private void pushGameUpdate() {
        try {
            StringBuilder sb = new StringBuilder();
            for (char[] row : board) {
                for (char cell : row) sb.append(cell);
            }
            String nextTurn = (myColor == 'B') ? "W" : "B";
            ApiClient.pushGameState(gameId, sb.toString(), nextTurn, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleAIMove() {
        if (aiMoveTask != null) aiMoveTask.cancel();

        aiMoveTask = new TimerTask() {
            public void run() {
                if (!gameEnded && !"B".equals(currentTurn)) { // It's AI's turn and game not ended
                    List<Point> aiMoves = validMoves('W');
                    if (!aiMoves.isEmpty()) {
                        Point move = selectAIMove(aiMoves, difficulty);
                        makeMove(move.x, move.y, 'W');
                        currentTurn = "B";
                        SwingUtilities.invokeLater(() -> {
                            repaint();
                            updateLabels();
                            scheduleAIMove(); // Human turn next, reschedule if needed
                            checkEnd();
                        });
                    } else {
                        // AI has no moves, but human might
                        List<Point> humanMoves = validMoves('B');
                        if (!humanMoves.isEmpty()) {
                            currentTurn = "B"; // Give turn back to player
                            SwingUtilities.invokeLater(() -> {
                                updateLabels();
                                checkEnd();
                            });
                        } else {
                            // Neither has moves
                            SwingUtilities.invokeLater(() -> {
                                checkEnd(); 
                            });
                        }
                    }
                }
            }
        };
        aiTimer.schedule(aiMoveTask, 1000);
        
    }

    private Point selectAIMove(List<Point> moves, String difficulty) {
        if (difficulty.equalsIgnoreCase("basic")) {
            // Random move
            return moves.get(new Random().nextInt(moves.size()));
        }
        if (difficulty.equalsIgnoreCase("intermediate")) {
            // 50% chance to pick a corner
            List<Point> corners = findCorners(moves);
            if (!corners.isEmpty() && Math.random() < 0.5) {
                return corners.get(new Random().nextInt(corners.size()));
            }
            return moves.get(new Random().nextInt(moves.size()));
        }
        if (difficulty.equalsIgnoreCase("advanced")) {
            // Always pick corner if available
            List<Point> corners = findCorners(moves);
            if (!corners.isEmpty()) {
                return corners.get(new Random().nextInt(corners.size()));
            }
            // Otherwise pick move that flips most pieces
            return findBestMove(moves, 'W');
        }
        // Fallback
        return moves.get(new Random().nextInt(moves.size()));
    }

    private List<Point> findCorners(List<Point> moves) {
        List<Point> corners = new ArrayList<>();
        for (Point move : moves) {
            if ((move.x == 0 || move.x == SIZE - 1) && (move.y == 0 || move.y == SIZE - 1)) {
                corners.add(move);
            }
        }
        return corners;
    }

    
    private Point findBestMove(List<Point> moves, char aiColor) {
        Point bestMove = moves.get(0);
        int maxFlipped = -1;
        for (Point move : moves) {
            int flipped = countFlipped(move.x, move.y, aiColor);
            if (flipped > maxFlipped) {
                maxFlipped = flipped;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int countFlipped(int row, int col, char player) {
        char opponent = (player == 'B') ? 'W' : 'B';
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        int flipped = 0;
    
        for (int d = 0; d < 8; d++) {
            int r = row + dr[d], c = col + dc[d];
            int tempFlipped = 0;
            while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == opponent) {
                tempFlipped++;
                r += dr[d];
                c += dc[d];
            }
            if (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == player) {
                flipped += tempFlipped;
            }
        }
        return flipped;
    }    
    

    private void checkForStuckPlayer() {
        if (isMultiplayer && !gameEnded) {
            boolean currentPlayerHasNoMoves = validMoves(currentTurn.charAt(0)).isEmpty();
            boolean opponentHasMoves = !validMoves(currentTurn.charAt(0) == 'B' ? 'W' : 'B').isEmpty();

            if (currentPlayerHasNoMoves && opponentHasMoves) {
                currentTurn = (currentTurn.equals("B")) ? "W" : "B";
                try {
                    StringBuilder sb = new StringBuilder();
                    for (char[] row : board) {
                        for (char cell : row) sb.append(cell);
                    }
                    ApiClient.pushGameState(gameId, sb.toString(), currentTurn, "");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void checkEnd() {
        if (gameEnded) return;

        boolean noBlackMoves = validMoves('B').isEmpty();
        boolean noWhiteMoves = validMoves('W').isEmpty();

        if (noBlackMoves && noWhiteMoves) {
            gameEnded = true;

            int blacks = countPieces('B');
            int whites = countPieces('W');

            String winner;
            String resultForServer = "draw";

            if (blacks > whites) {
                winner = (aiGame ? "You win!" : "Black wins!");
                resultForServer = (aiGame ? "win" : (myColor == 'B' ? "win" : "loss"));
            } else if (whites > blacks) {
                winner = (aiGame ? "Computer wins!" : "White wins!");
                resultForServer = (aiGame ? "loss" : (myColor == 'W' ? "win" : "loss"));
            } else {
                winner = "It's a Draw!";
            }

            JOptionPane.showMessageDialog(this, winner + "  B:" + blacks + " W:" + whites);

            if (isMultiplayer) {
                ApiClient.recordResult(ApiClient.username, resultForServer);
            }

            closeGame();
        }
    }

    private void closeGame() {
        if (isMultiplayer && gameId != null) {
            try {
                ApiClient.deactivateGame(gameId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (syncTimer != null) syncTimer.cancel();
        if (aiTimer != null) aiTimer.cancel();
        dispose();
        new HomeScreen();
    }

    private class BoardPanel extends JPanel {
        public BoardPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (aiGame && !"B".equals(currentTurn)) {
                        JOptionPane.showMessageDialog(OthelloBoard.this, "Wait for your turn!");
                        return;
                    } else if (!aiGame && !isMyTurn()) {
                        JOptionPane.showMessageDialog(OthelloBoard.this, "Not your turn!");
                        return;
                    }

                    int cellSize = Math.min(getWidth(), getHeight()) / SIZE;
                    int xOffset = (getWidth() - (cellSize * SIZE)) / 2;
                    int yOffset = (getHeight() - (cellSize * SIZE)) / 2;
                    int row = (e.getY() - yOffset) / cellSize;
                    int col = (e.getX() - xOffset) / cellSize;

                    if (row < 0 || col < 0 || row >= SIZE || col >= SIZE) return;

                    if (makeMove(row, col, myColor)) {
                        repaint();
                        updateLabels();

                        if (aiGame) {
                            currentTurn = "W";
                            updateLabels();
                            scheduleAIMove();
                        } else if (isMultiplayer) {
                            pushGameUpdate();
                        }

                        checkEnd();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0x11, 0x5C, 0x52));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int cellSize = Math.min(getWidth(), getHeight()) / SIZE;
            int xOffset = (getWidth() - (cellSize * SIZE)) / 2;
            int yOffset = (getHeight() - (cellSize * SIZE)) / 2;

            g2.setColor(Color.BLACK);
            for (int i = 0; i <= SIZE; i++) {
                g2.drawLine(xOffset, yOffset + i * cellSize, xOffset + SIZE * cellSize, yOffset + i * cellSize);
                g2.drawLine(xOffset + i * cellSize, yOffset, xOffset + i * cellSize, yOffset + SIZE * cellSize);
            }

            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (board[r][c] == 'B') {
                        g2.setColor(Color.BLACK);
                        g2.fillOval(xOffset + c * cellSize + 5, yOffset + r * cellSize + 5, cellSize - 10, cellSize - 10);
                    } else if (board[r][c] == 'W') {
                        g2.setColor(Color.WHITE);
                        g2.fillOval(xOffset + c * cellSize + 5, yOffset + r * cellSize + 5, cellSize - 10, cellSize - 10);
                    }
                }
            }

            g2.setColor(Color.LIGHT_GRAY);
            for (Point p : validMoves(myColor)) {
                g2.drawOval(xOffset + p.y * cellSize + 10, yOffset + p.x * cellSize + 10, cellSize - 20, cellSize - 20);
            }
        }
    }
}
