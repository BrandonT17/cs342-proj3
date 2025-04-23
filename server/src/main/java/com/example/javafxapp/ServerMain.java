package com.example.javafxapp;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class ServerMain {
    private int port;
    private ServerSocket serverSocket;
    private GameManager gameManager;
    private ServerGUI gui;
    private volatile boolean running;
    private Set<String> activeUsernames = ConcurrentHashMap.newKeySet();

    public ServerMain(ServerGUI gui) {
        this.port = 5001;
        this.gameManager = new GameManager();
        this.gui = gui;
        this.running = false;
    }

    public void start() {
        running = true;
        try {
            serverSocket = new ServerSocket(port);
            gui.appendToLog("Connecting to port 127.0.0.1...");
            gui.appendToLog("Successfully connected to port 127.0.0.1");
            
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    handleNewConnection(socket);
                } catch (IOException e) {
                    if (running) {
                        gui.appendToLog("Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            gui.appendToLog("Server error: " + e.getMessage());
        }
    }

    private void handleNewConnection(Socket socket) {
        ClientHandler client = new ClientHandler(socket, this);
        new Thread(client).start();
        gameManager.addPlayer(client);
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    public void playerConnected(String playerName) {
        gui.addPlayer(playerName);
        gui.appendToLog("Player " + playerName + " has connected to the server.");
    }

    public void playerDisconnected(String playerName) {
        gui.removePlayer(playerName);
        gui.appendToLog("Player " + playerName + " has left the server.");
    }

    public GameManager getGameManager() {
        return gameManager;
    }

        public boolean isUsernameTaken(String username) {
            return activeUsernames.contains(username);
    }

    public synchronized boolean registerUsername(String username) {
        if (activeUsernames.contains(username)) return false;
        activeUsernames.add(username);
        return true;
    }

    public synchronized void unregisterUsername(String username) {
        activeUsernames.remove(username);
    }
}
