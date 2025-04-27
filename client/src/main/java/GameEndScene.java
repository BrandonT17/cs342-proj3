package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameEndScene {
    public static Scene create(SceneManager sceneManager) {
        Image image = new Image(GameEndScene.class.getResourceAsStream("/connect4logo.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        HBox imgBox = new HBox(imageView);
        imgBox.setAlignment(Pos.TOP_LEFT);
        imgBox.setPadding(new Insets(10)); // remove any spacing
        imgBox.setPickOnBounds(false);

        Label gameEndMessage = new Label("Congratulations...");
        gameEndMessage.setTextFill(Color.WHITE);
        gameEndMessage.setStyle("-fx-font-size: 20px;");

        Button playAgain = new Button("Play Again");
        playAgain.setOnAction(e -> sceneManager.showGameplay());

        Button quit = new Button("Quit Game");
        quit.setOnAction(e -> sceneManager.showMainMenu());

        VBox centerLayout = new VBox(20, gameEndMessage, playAgain, quit);
        centerLayout.setAlignment(Pos.CENTER);

        BorderPane contentPane = new BorderPane();
        contentPane.setCenter(centerLayout);

        StackPane root = new StackPane();
        root.getChildren().addAll(contentPane, imgBox);
        StackPane.setAlignment(imgBox, Pos.TOP_LEFT);

        root.setStyle("-fx-background-color: linear-gradient(to bottom, #3a9bdc, #0d58a6);");

        return new Scene(root, 800, 600);
    }
}

