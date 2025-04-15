package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class ConnectingScene {
    public static Scene create(SceneManager sceneManager) {
        Image image = new Image(ConnectingScene.class.getResourceAsStream("/connect4logo.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        Label caption = new Label("JavaFX Edition");
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        Label status = new Label("Waiting to connect to server...");
        Button goBack = new Button("Go Back");
        goBack.setOnAction(e -> sceneManager.showMainMenu());

        VBox box = new VBox(imageView, caption, progressBar, status, goBack);
        box.setAlignment(Pos.CENTER);
        return new Scene(box, 700, 500);
    }
}

