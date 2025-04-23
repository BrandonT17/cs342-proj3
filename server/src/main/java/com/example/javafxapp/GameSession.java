package com.example.javafxapp;


public class GameSession {
    private String gameId;
    private ClientHandler player1;
    private ClientHandler player2;
    private GameLogic gameBoard;
    private int currentTurn;

    public GameSession(ClientHandler player1, ClientHandler player2) {
        this.gameId = java.util.UUID.randomUUID().toString();
        this.player1 = player1;
        this.player2 = player2;
        this.gameBoard = new GameLogic();
        this.currentTurn = 1;
        
        player1.setCurrentGame(this);
        player2.setCurrentGame(this);
    }

    public void startGame() {
        Message startMessage = new Message(MessageType.GAME_START, "Game starting", "Server");
        broadcastMessage(startMessage);
    }

    public void processMove(ClientHandler player, int column) {
        if (isPlayerTurn(player)) {
            int playerNumber = (player == player1) ? 1 : 2;
            if (gameBoard.makeMove(column, playerNumber)) {
                sendGameUpdate();
                currentTurn = (currentTurn == 1) ? 2 : 1;
            }
        }
    }

    public void sendGameUpdate() {
        String boardState = gameBoard.getBoardState();
        Message updateMessage = new Message(MessageType.GAME_UPDATE, boardState, "Server");
        broadcastMessage(updateMessage);
    }

    private boolean isPlayerTurn(ClientHandler player) {
        return (player == player1 && currentTurn == 1) || (player == player2 && currentTurn == 2);
    }

    public void broadcastMessage(Message message) {
        player1.sendMessage(message);
        player2.sendMessage(message);
    }
} 
