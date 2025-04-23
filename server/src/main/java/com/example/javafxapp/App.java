package com.example.javafxapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private ServerMain server;
    private ServerGUI serverGUI;

    @Override
    public void start(Stage stage) {
        // Create the GUI
        serverGUI = new ServerGUI();
        Scene scene = new Scene(serverGUI, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Server GUI");
        stage.show();

        // Start the server in a separate thread
        server = new ServerMain(serverGUI);
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
