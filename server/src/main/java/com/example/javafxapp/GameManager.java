package com.example.javafxapp;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class GameManager {
    private final Queue<ClientHandler> waitingPlayers;
    private final List<GameSession> activeGames;

    public GameManager() {
        this.waitingPlayers = new ConcurrentLinkedQueue<>();
        this.activeGames = new ArrayList<>();
    }

    public synchronized void addPlayer(ClientHandler client) {
        Objects.requireNonNull(client, "Client cannot be null");
        
        if (waitingPlayers.contains(client) || client.getUsername() == null) {
            System.out.println("[MATCHMAKING] Ignoring duplicate or unauthenticated client");
            return;
        }

        waitingPlayers.add(client);
        System.out.println("[MATCHMAKING] Added " + client.getUsername() + " to queue (" 
                         + waitingPlayers.size() + " waiting)");
        matchPlayers();
    }

    public synchronized void removePlayer(ClientHandler client) {
        if (waitingPlayers.remove(client)) {
            System.out.println("[MATCHMAKING] Removed " + client.getUsername() + " from queue");
        }
    }

    private synchronized void matchPlayers() {
        while (waitingPlayers.size() >= 2) {
            ClientHandler player1 = waitingPlayers.poll();
            ClientHandler player2 = waitingPlayers.poll();

            if (player1 == null || player2 == null) {
                System.out.println("[MATCHMAKING] Error: Null player in queue");
                continue;
            }

            if (player1.equals(player2)) {
                System.out.println("[MATCHMAKING] Error: Attempted to match player with themselves");
                waitingPlayers.add(player1); // Return one player to queue
                continue;
            }

            System.out.println("[MATCHMAKING] Creating game between " 
                            + player1.getUsername() + " and " + player2.getUsername());

            try {
                GameSession game = new GameSession(player1, player2);
                activeGames.add(game);

                Message startMsg = new Message(
                    MessageType.MATCH_FOUND,
                    player1.getUsername() + " vs " + player2.getUsername(),
                    "Server"
                );

                player1.sendMessage(startMsg);
                player2.sendMessage(startMsg);

                broadcastServerMessage("Game started between " 
                                    + player1.getUsername() + " and " 
                                    + player2.getUsername());
            } catch (IllegalArgumentException e) {
                System.out.println("[MATCHMAKING] Error creating game: " + e.getMessage());
                waitingPlayers.add(player1);
                waitingPlayers.add(player2);
            }
        }
    }

    public synchronized void broadcastServerMessage(String message) {
        Message serverMessage = new Message(MessageType.CHAT, message, "Server");
        
        for (ClientHandler player : waitingPlayers) {
            try {
                player.sendMessage(serverMessage);
            } catch (Exception e) {
                System.out.println("[BROADCAST] Error sending to " + player.getUsername());
            }
        }
        
        for (GameSession game : activeGames) {
            try {
                game.broadcastMessage(serverMessage);
            } catch (Exception e) {
                System.out.println("[BROADCAST] Error broadcasting to game session");
            }
        }
    }

    public synchronized void removeGame(GameSession game) {
        if (activeGames.remove(game)) {
            System.out.println("[MATCHMAKING] Removed completed game session");
        }
    }
}
