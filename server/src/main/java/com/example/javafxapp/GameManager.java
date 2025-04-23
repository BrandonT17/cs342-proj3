package com.example.javafxapp;


import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class GameManager {
    private Queue<ClientHandler> waitingPlayers;
    private List<GameSession> activeGames;

    public GameManager() {
        this.waitingPlayers = new LinkedList<>();
        this.activeGames = new ArrayList<>();
    }

    public void addPlayer(ClientHandler client) {
        waitingPlayers.add(client);
        matchPlayers();
    }

    public void removePlayer(ClientHandler client) {
        waitingPlayers.remove(client);
    }

    private void matchPlayers() {
        if (waitingPlayers.size() >= 2) {
            ClientHandler player1 = waitingPlayers.poll();
            ClientHandler player2 = waitingPlayers.poll();
            GameSession game = new GameSession(player1, player2);
            activeGames.add(game);

            Message startMsg = new Message(MessageType.MATCH_FOUND, "", "Server");
            player1.sendMessage(startMsg);
            player2.sendMessage(startMsg);

            broadcastServerMessage("Game started between " + player1.getUsername() + " and " + player2.getUsername());
        }
    }

    public void broadcastServerMessage(String message) {
        Message serverMessage = new Message(MessageType.CHAT, message, "Server");
        for (ClientHandler player : waitingPlayers) {
            player.sendMessage(serverMessage);
        }
        for (GameSession game : activeGames) {
            game.broadcastMessage(serverMessage);
        }
    }
} 
