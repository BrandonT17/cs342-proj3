package server;

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
                server.unregisterUsername(username);
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
                /*this.username = message.getSender();
                server.playerConnected(username);
                break;*/
                String desiredUsername = message.getSender();
                if (server.registerUsername(desiredUsername)) {
                    this.username = desiredUsername;
                    server.playerConnected(username);
                    sendMessage(new Message(MessageType.CONNECT_ACK, "Welcome!", "Server"));
                } else {
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
                        System.out.println("Error: " + e.getMessage());
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
