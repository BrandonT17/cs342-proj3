package com.example.javafxapp;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerGUI extends BorderPane {
    private ListView<String> onlinePlayersListView;
    private TextArea serverOutputArea;
    private TextField inputField;
    private Button sendButton;
    private ObservableList<String> onlinePlayers;
    
    private ServerMain server; // ✅ Added reference to ServerMain

    public ServerGUI(ServerMain server) { // ✅ Accept ServerMain as constructor arg
        this.server = server;

        // Initialize components
        onlinePlayers = FXCollections.observableArrayList();
        onlinePlayersListView = new ListView<>(onlinePlayers);
        serverOutputArea = new TextArea();
        inputField = new TextField();
        sendButton = new Button("Send");

        // Configure components
        serverOutputArea.setEditable(false);
        serverOutputArea.setWrapText(true);
        onlinePlayersListView.setPrefWidth(200);

        // Create left panel with title
        VBox leftPanel = new VBox(5);
        Label playersLabel = new Label("Online Players");
        leftPanel.getChildren().addAll(playersLabel, onlinePlayersListView);
        leftPanel.setPadding(new Insets(10));

        // Create right panel
        VBox rightPanel = new VBox(5);
        Label outputLabel = new Label("Server Output");

        // Create input panel
        HBox inputPanel = new HBox(5);
        inputPanel.getChildren().addAll(inputField, sendButton);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        rightPanel.getChildren().addAll(outputLabel, serverOutputArea, inputPanel);
        rightPanel.setPadding(new Insets(10));
        VBox.setVgrow(serverOutputArea, Priority.ALWAYS);

        // Set up the layout
        setLeft(leftPanel);
        setCenter(rightPanel);

        // Add handlers
        sendButton.setOnAction(e -> handleSendMessage());
    }

    public synchronized void addPlayer(ClientHandler client) {
        Platform.runLater(() -> {
            if (!onlinePlayers.contains(client.getUsername())) {
                onlinePlayers.add(client.getUsername());
                server.getGameManager().addPlayer(client); // ✅ Delegates to GameManager
                System.out.println("[SERVER] Added " + client.getUsername() + " to waiting queue.");
            }
        });
    }

    public void removePlayer(String playerName) {
        Platform.runLater(() -> onlinePlayers.remove(playerName));
    }

    public void appendToLog(String message) {
        Platform.runLater(() -> {
            serverOutputArea.appendText("[" +
                String.format("%02d:%02d:00", java.time.LocalTime.now().getHour(),
                java.time.LocalTime.now().getMinute()) + "] " + message + "\n");
        });
    }

    private void handleSendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            appendToLog("Server: " + message);
            inputField.clear();
        }
    }
}

