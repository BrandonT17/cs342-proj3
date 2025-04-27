package com.example.javafxapp;

import java.util.Objects;

public class GameSession {
    private final String gameId;
    private final ClientHandler player1;
    private final ClientHandler player2;
    private final GameLogic gameBoard;
    private int currentTurn; // 1 = player1, 2 = player2
    private boolean gameActive;

    public GameSession(ClientHandler player1, ClientHandler player2) {
        Objects.requireNonNull(player1, "Player1 cannot be null");
        Objects.requireNonNull(player2, "Player2 cannot be null");
        
        if (player1.equals(player2)) {
            throw new IllegalArgumentException("Cannot create game with identical players");
        }

        this.gameId = java.util.UUID.randomUUID().toString();
        this.player1 = player1;
        this.player2 = player2;
        this.gameBoard = new GameLogic();
        this.currentTurn = 1;
        this.gameActive = true;

        System.out.println("[GAME] Created session " + gameId + 
                         " between " + player1.getUsername() + 
                         " (P1) and " + player2.getUsername() + " (P2)");

        startGame();
    }

    public synchronized void startGame() {
        // Send game start with player order
        String playerNames = player1.getUsername() + " vs " + player2.getUsername();
        broadcastMessage(new Message(MessageType.GAME_START, playerNames, "Server"));
        
        // Forcefully set first turn to player1
        this.currentTurn = 1;
        System.out.println("[GAME] First turn granted to " + player1.getUsername());
        player1.sendMessage(new Message(MessageType.YOUR_TURN, "", "Server"));
        player2.sendMessage(new Message(MessageType.WAIT, "", "Server"));
    }

    public synchronized void processMove(ClientHandler player, int column) {
        if (!gameActive) {
            System.out.println("[GAME] Move rejected - game ended");
            return;
        }

        System.out.println("[GAME] Move attempt by " + player.getUsername() + 
                         " (Expected turn: " + currentTurn + ")");

        if (!isPlayerTurn(player)) {
            System.out.println("[GAME] Wrong turn for " + player.getUsername());
            player.sendMessage(new Message(MessageType.ERROR, "Not your turn", "Server"));
            return;
        }

        int playerNumber = (player == player1) ? 1 : 2;
        if (!gameBoard.makeMove(column, playerNumber)) {
            System.out.println("[GAME] Invalid move in column " + column);
            player.sendMessage(new Message(MessageType.ERROR, "Column full", "Server"));
            return;
        }

        // Broadcast valid move
        Message moveMsg = new Message(
            MessageType.MOVE_VALID, 
            playerNumber + "," + column, 
            "Server"
        );
        broadcastMessage(moveMsg);

        // Check game state
        if (gameBoard.checkWin(playerNumber)) {
            endGame("Player " + playerNumber + " wins!");
        } else if (gameBoard.isDraw()) {
            endGame("Draw!");
        } else {
            switchTurn();
        }
    }

    private synchronized void switchTurn() {
        currentTurn = (currentTurn == 1) ? 2 : 1;
        System.out.println("[GAME] Turn switched to " + 
                         (currentTurn == 1 ? player1.getUsername() : player2.getUsername()));
        
        if (currentTurn == 1) {
            player1.sendMessage(new Message(MessageType.YOUR_TURN, "", "Server"));
            player2.sendMessage(new Message(MessageType.WAIT, "", "Server"));
        } else {
            player2.sendMessage(new Message(MessageType.YOUR_TURN, "", "Server"));
            player1.sendMessage(new Message(MessageType.WAIT, "", "Server"));
        }
    }

    private void endGame(String message) {
        gameActive = false;
        System.out.println("[GAME] Game ended: " + message);
        broadcastMessage(new Message(MessageType.GAME_END, message, "Server"));
        player1.setCurrentGame(null);
        player2.setCurrentGame(null);
    }

    public synchronized void handleDisconnect(ClientHandler disconnectedPlayer) {
        if (!gameActive) return;
        
        gameActive = false;
        ClientHandler remainingPlayer = (disconnectedPlayer == player1) ? player2 : player1;
        
        System.out.println("[GAME] " + disconnectedPlayer.getUsername() + " disconnected");
        
        if (remainingPlayer != null) {
            remainingPlayer.sendMessage(new Message(
                MessageType.GAME_END,
                "Opponent disconnected",
                "Server"
            ));
            remainingPlayer.setCurrentGame(null);
        }
    }

    private boolean isPlayerTurn(ClientHandler player) {
        return (player == player1 && currentTurn == 1) || 
               (player == player2 && currentTurn == 2);
    }

    public void broadcastMessage(Message message) {
        try {
            player1.sendMessage(message);
        } catch (Exception e) {
            System.err.println("[GAME] Failed to send to " + player1.getUsername());
        }
        
        try {
            player2.sendMessage(message);
        } catch (Exception e) {
            System.err.println("[GAME] Failed to send to " + player2.getUsername());
        }
    }
}
