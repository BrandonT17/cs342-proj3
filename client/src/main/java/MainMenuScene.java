package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MainMenuScene {
    public static Scene create(SceneManager sceneManager) {
        // app logo
        Image image = new Image(MainMenuScene.class.getResourceAsStream("/connect4logo.png"));
        ImageView imageView = new ImageView(image);
            imageView.setFitWidth(300);
            imageView.setPreserveRatio(true);

        // username
        Label prompt = new Label("Enter your username:");
            prompt.setFont(new Font(18));
            prompt.setTextFill(Color.WHITE);
        
        TextField username = new TextField();
            username.setPromptText("Type username here");
            username.setMaxWidth(300);
        // username validation
        String invalid = "Username not available!";
        String valid = "Username available.";
        Label validity = new Label(valid);
            validity.setTextFill(Color.WHITE);
        // send game to connection screen
        Button connect = new Button("Connect");
        connect.setOnAction(e -> {
            // TODO: validate username and connect to server
            // sceneManager.showConnecting();
            // sceneManager.showGameplay();
            sceneManager.showGameEnd();
        });

        // layout
        VBox mainLayout = new VBox(imageView, prompt, username, validity, connect);
            mainLayout.setSpacing(20);
            mainLayout.setAlignment(Pos.CENTER);
            mainLayout.setPadding(new Insets(20));
            mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #3a9bdc, #0d58a6);");

        // pass layout to scene
        return new Scene(mainLayout, 800, 600);
    }
}

