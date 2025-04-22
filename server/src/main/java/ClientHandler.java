<<<<<<< Updated upstream:server/src/main/java/ClientHandler.java
package com.example.server;

import com.example.javafxapp.Message;
=======
package com.example.javafxapp;

>>>>>>> Stashed changes:server/src/main/java/com/example/javafxapp/ClientHandler.java
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
<<<<<<< Updated upstream:server/src/main/java/ClientHandler.java
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // IMPORTANT: output stream must be created first
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                // Read a message
                Message msg = (Message) in.readObject();
                System.out.println("Received: " + msg.getType() + " from " + msg.getSender());

                // Process the message based on type
                switch (msg.getType()) {
                    case MOVE:
                        handleMove(msg);
                        break;
                    case CHAT:
                        handleChat(msg);
                        break;
                    default:
                        System.out.println("Unknown message type.");
                        break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    private void handleMove(Message moveMsg) {
        System.out.println("Processing move: " + moveMsg.getMessage());
        // TODO: Update the board, check for win, send updates to clients
    }

    private void handleChat(Message chatMsg) {
        System.out.println("Processing chat: " + chatMsg.getMessage());
        // TODO: Relay chat message to opponent
    }

    // Allow server to send messages back
    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }
}

=======
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
            input = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                Message message = (Message) input.readObject();
                handleMessage(message);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            disconnect();
        }
    }

    public void sendMessage(Message message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            disconnect();
        }
    }

    public void disconnect() {
        try {
            if (username != null) {
                server.playerDisconnected(username);
            }
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void setCurrentGame(GameSession game) {
        this.currentGame = game;
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case CONNECT:
                this.username = message.getSender();
                server.playerConnected(username);
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
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                break;
        }
    }

    public String getUsername() {
        return username;
    }
} 
>>>>>>> Stashed changes:server/src/main/java/com/example/javafxapp/ClientHandler.java
