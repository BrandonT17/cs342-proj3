package com.example.javafxapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private ServerMain server;
    private ServerGUI serverGUI;

    @Override
    public void start(Stage stage) {
        // Create server first
        server = new ServerMain(); // Use default constructor
        serverGUI = new ServerGUI(server); // Pass server to GUI
        server.setGUI(serverGUI);          // Link GUI to server

        // GUI setup
        Scene scene = new Scene(serverGUI, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Server GUI");
        stage.show();

        // Run server in new thread
        Thread serverThread = new Thread(() -> server.start());
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

