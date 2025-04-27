package com.example.javafxapp;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;
    private GameSession currentGame;
    private final ServerMain server;
    private volatile boolean running = true;

    public ClientHandler(Socket socket, ServerMain server) {
        this.socket = Objects.requireNonNull(socket, "Socket cannot be null");
        this.server = Objects.requireNonNull(server, "Server cannot be null");
        initializeStreams();
    }

    private void initializeStreams() {
        try {
            // Initialize output FIRST to prevent deadlock
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.output.flush();
            this.input = new ObjectInputStream(socket.getInputStream());
            System.out.println("[SERVER] Streams initialized for client");
        } catch (IOException e) {
            System.err.println("[SERVER] Error initializing streams: " + e.getMessage());
            closeResources();
        }
    }

    @Override
    public void run() {
        try {
            while (running && !socket.isClosed()) {
                Message message = (Message) input.readObject();
                System.out.println("[SERVER] Received " + message.getType() + 
                                  " from " + (username != null ? username : "new client"));
                handleMessage(message);
            }
        } catch (EOFException e) {
            System.out.println("[SERVER] Client " + username + " disconnected normally");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SERVER] Error in client handler: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    public synchronized void sendMessage(Message message) {
        if (!running || socket.isClosed()) {
            System.out.println("[SERVER] Cannot send - connection closed");
            return;
        }

        try {
            output.writeObject(message);
            output.flush();
            System.out.println("[SERVER] Sent " + message.getType() + 
                             " to " + username);
        } catch (IOException e) {
            System.err.println("[SERVER] Failed to send to " + username);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        if (!running) return;
        running = false;

        try {
            if (currentGame != null) {
                currentGame.handleDisconnect(this);
            }

            if (username != null) {
                server.unregisterUsername(username);
                server.playerDisconnected(username);
                System.out.println("[SERVER] Disconnected " + username);
            }
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("[SERVER] Error closing resources: " + e.getMessage());
        }
    }

    public synchronized void setCurrentGame(GameSession game) {
        this.currentGame = game;
        if (game != null) {
            System.out.println("[SERVER] Assigned " + username + " to game session");
        }
    }

    private void handleMessage(Message message) {
        if (message == null) {
            System.err.println("[SERVER] Received null message");
            return;
        }

        try {
            switch (message.getType()) {
                case CONNECT:
                    handleConnect(message);
                    break;

                case DISCONNECT:
                    disconnect();
                    break;

                case CHAT:
                    if (currentGame != null) {
                        System.out.println("[SERVER] Forwarding chat from " + username);
                        currentGame.broadcastMessage(message);
                    }
                    break;

                case MOVE:
                    if (currentGame != null) {
                        handleMove(message);
                    }
                    break;

                default:
                    System.err.println("[SERVER] Unhandled message type: " + message.getType());
            }
        } catch (Exception e) {
            System.err.println("[SERVER] Error handling message: " + e.getMessage());
        }
    }

    private void handleConnect(Message message) throws IOException {
        String desiredUsername = message.getSender().trim();
        System.out.println("[SERVER] Connection request from: " + desiredUsername);

        if (desiredUsername.isEmpty()) {
            sendMessage(new Message(MessageType.ERROR, "Username required", "Server"));
            return;
        }

        if (server.registerUsername(desiredUsername)) {
            this.username = desiredUsername;
            server.playerConnected(username, this);
            sendMessage(new Message(MessageType.CONNECT_ACK, "Welcome", "Server"));
            server.getGameManager().addPlayer(this);
        } else {
            sendMessage(new Message(MessageType.USERNAME_TAKEN, "Username taken", "Server"));
        }
    }

    private void handleMove(Message message) throws IOException {
        try {
            int column = Integer.parseInt(message.getMessage());
            System.out.println("[SERVER] Forwarding move to game session");
            currentGame.processMove(this, column);
        } catch (NumberFormatException e) {
            sendMessage(new Message(MessageType.ERROR, "Invalid move format", "Server"));
        }
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized boolean isConnected() {
        return running && !socket.isClosed();
    }
}
