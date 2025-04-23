package com.example.javafxapp;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;
    private GameSession currentGame;
    private ServerMain server;

    public ClientHandler(Socket socket, ServerMain server) {
        this.socket = socket;
        this.server = server;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); // Send stream header
            input = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Error setting up streams: " + e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                Message message = (Message) input.readObject();
                handleMessage(message);
            }
        } catch (Exception e) {
            System.out.println("Error in run(): " + e.getMessage());
            disconnect();
        }
    }

    public void sendMessage(Message message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (Exception e) {
            System.out.println("Error sending message to " + username + ": " + e.getMessage());
            disconnect();
        }
    }

    public void disconnect() {
        try {
            if (username != null) {
                server.unregisterUsername(username);
                server.playerDisconnected(username);
            }
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.out.println("Error during disconnect: " + e.getMessage());
        }
    }

    public void setCurrentGame(GameSession game) {
        this.currentGame = game;
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case CONNECT:
                String desiredUsername = message.getSender();
                System.out.println("[SERVER] CONNECT request from: " + desiredUsername);
                if (server.registerUsername(desiredUsername)) {
                    this.username = desiredUsername;
                    server.playerConnected(username);
                    System.out.println("[SERVER] Username accepted: " + username);
                    sendMessage(new Message(MessageType.CONNECT_ACK, "Welcome!", "Server"));

                    server.getGameManager().addPlayer(this);
                } else {
                    System.out.println("[SERVER] Username taken: " + desiredUsername);
                    sendMessage(new Message(MessageType.ERROR, "Username already taken", "Server"));
                }
                break;

            case DISCONNECT:
                disconnect();
                break;

            case CHAT:
                if (currentGame != null) {
                    currentGame.broadcastMessage(message);
                }
                break;

            case MOVE:
                if (currentGame != null) {
                    try {
                        int column = Integer.parseInt(message.getMessage());
                        currentGame.processMove(this, column);
                    } catch (Exception e) {
                        System.out.println("Error parsing move: " + e.getMessage());
                    }
                }
                break;

            case CHECK_USERNAME:
                String nameToCheck = message.getSender();
                boolean taken = server.isUsernameTaken(nameToCheck);
                Message reply = new Message(
                    taken ? MessageType.USERNAME_TAKEN : MessageType.USERNAME_AVAILABLE,
                    nameToCheck, "Server"
                );
                sendMessage(reply);
                break;
        }
    }

    public String getUsername() {
        return username;
    }
}

