package com.example.server;

import com.example.javafxapp.Message;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                System.out.println("Waiting for clients...");

                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                // Start a new thread to handle this client
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

