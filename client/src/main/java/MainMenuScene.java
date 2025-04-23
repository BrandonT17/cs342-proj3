package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
import com.example.javafxapp.Message;
import com.example.javafxapp.MessageType;
import com.example.javafxapp.network.NetworkClient;

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
        // App logo
        Image image = new Image(MainMenuScene.class.getResourceAsStream("/connect4logo.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        // Username prompt and input
        Label prompt = new Label("Enter your username:");
        prompt.setFont(new Font(18));
        prompt.setTextFill(Color.WHITE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Type username here");
        usernameField.setMaxWidth(300);

        // Status label (valid/invalid)
        Label validity = new Label();
        validity.setTextFill(Color.WHITE);

        // Connect button
        Button connect = new Button("Connect");

        // On click, try to connect and send username
        connect.setOnAction(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                validity.setText("Username cannot be empty.");
                validity.setTextFill(Color.RED);
                return;
            }

            try {
                NetworkClient client = new NetworkClient("127.0.0.1", 5001);
                Message connectMsg = new Message(MessageType.CONNECT, "", username);
                client.sendMessage(connectMsg);

                Message response = client.readMessage();

                if (response.getType() == MessageType.CONNECT_ACK) {
                    validity.setText("Username accepted!");
                    validity.setTextFill(Color.LIGHTGREEN);
                    sceneManager.setClient(client);
                    sceneManager.setUsername(username);
                    sceneManager.showConnecting();
                } else if (response.getType() == MessageType.ERROR) {
                    validity.setText(response.getMessage());
                    validity.setTextFill(Color.RED);
                    client.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                validity.setText("Connection failed: " + ex.getMessage());
                validity.setTextFill(Color.RED);
            }
        });

        // Layout setup
        VBox mainLayout = new VBox(imageView, prompt, usernameField, validity, connect);
        mainLayout.setSpacing(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #3a9bdc, #0d58a6);");

        return new Scene(mainLayout, 800, 600);
    }
}

