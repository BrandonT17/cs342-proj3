package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class GameEndScene {
    public static Scene create(SceneManager sceneManager) {
        TextField gameEndMessage = new TextField("Congratulations...");
        Button playAgain = new Button("Play Again");
        playAgain.setOnAction(e -> sceneManager.showGameplay());

        Button quit = new Button("Quit Game");
        quit.setOnAction(e -> System.exit(0));

        VBox box = new VBox(gameEndMessage, playAgain, quit);
        box.setAlignment(Pos.CENTER);
        return new Scene(box, 700, 500);
    }
}

