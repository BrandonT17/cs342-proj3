package com.example.javafxapp;

import com.example.javafxapp.scenes.*;
import javafx.stage.Stage;

public class SceneManager {
    private Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

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

