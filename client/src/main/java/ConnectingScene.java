package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ConnectingScene {
    public static Scene create(SceneManager sceneManager) {
        // logo image
        Image image = new Image(ConnectingScene.class.getResourceAsStream("/connect4logo.png"));
        ImageView imageView = new ImageView(image);
            imageView.setFitWidth(300);
            imageView.setPreserveRatio(true);
        ProgressBar progressBar = new ProgressBar(0);
            progressBar.setPrefWidth(300);
        
        Label status = new Label("Waiting to connect to server...");
            status.setTextFill(Color.WHITE);
        Button goBack = new Button("Go Back");
            goBack.setOnAction(e -> sceneManager.showMainMenu());
        
        // loading bar
        VBox mainLayout = new VBox(imageView, status, progressBar, goBack);
            mainLayout.setSpacing(20);
            mainLayout.setAlignment(Pos.CENTER);
            mainLayout.setPadding(new Insets(20));
            mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #3a9bdc, #0d58a6);");

        return new Scene(mainLayout, 800, 600);
    }
}

