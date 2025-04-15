package com.example.javafxapp;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showMainMenu(); // show main menu by default
    }

    public static void main(String[] args) {
        launch(args);
    }
}


