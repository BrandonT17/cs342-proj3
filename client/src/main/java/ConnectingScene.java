package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
import com.example.javafxapp.Message;
import com.example.javafxapp.MessageType;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ConnectingScene {
    public static Scene create(SceneManager sceneManager) {
        // logo
        Image image = new Image(ConnectingScene.class.getResourceAsStream("/connect4logo.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        // status label
        Label status = new Label("Waiting for an opponent...");
        status.setTextFill(Color.WHITE);
        status.setFont(new Font(16));

        // go back button
        Button goBack = new Button("Go Back");
        goBack.setOnAction(e -> sceneManager.showMainMenu());

        // layout
        VBox mainLayout = new VBox(imageView, status, goBack);
        mainLayout.setSpacing(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #3a9bdc, #0d58a6);");

        Scene scene = new Scene(mainLayout, 800, 600);

        // background thread to wait for match
        new Thread(() -> {
            try {
                while (true) {
                    Message msg = sceneManager.getClient().readMessage();
                    if (msg.getType() == MessageType.MATCH_FOUND) {
                        Platform.runLater(() -> {
                            sceneManager.showGameplay();
                        });
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    status.setText("Error: Lost connection to server.");
                    status.setTextFill(Color.RED);
                });
            }
        }).start();

        return scene;
    }
}

