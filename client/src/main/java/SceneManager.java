package com.example.javafxapp;

import com.example.javafxapp.network.NetworkClient;
import com.example.javafxapp.scenes.*;
import javafx.stage.Stage;

public class SceneManager {
    private Stage stage;

    // ✅ Add these fields
    private NetworkClient client;
    private String username;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    // ✅ Setters and Getters
    public void setClient(NetworkClient client) {
        this.client = client;
    }

    public NetworkClient getClient() {
        return client;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    // Scene transitions
    public void showMainMenu() {
        stage.setScene(MainMenuScene.create(this));
        stage.setTitle("Connect 4 - JavaFX Edition");
        stage.show();
    }

    public void showConnecting() {
        stage.setScene(ConnectingScene.create(this));
    }

    public void showGameplay() {
        stage.setScene(GameplayScene.create(this));
    }

    public void showGameEnd() {
        stage.setScene(GameEndScene.create(this));
    }
}

