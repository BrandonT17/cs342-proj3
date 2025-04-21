package com.example.server;

import com.example.javafxapp.Message;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
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

