package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class GameplayScene {
    private static final int ROWS = 6;
    private static final int COLS = 7;

    public static Scene create(SceneManager sceneManager) {
        // Round counter
        SimpleIntegerProperty round = new SimpleIntegerProperty(1);

        // Round display
        Label roundLabel = new Label();
        roundLabel.setFont(new Font(24));
        roundLabel.setTextFill(Color.WHITE);
        roundLabel.textProperty().bind(round.asString("ROUND %d"));

        // Player 1 VBox
        Circle redCircle = new Circle(30, Color.RED);
        Label p1Name = new Label("John_doe_224");
        Label p1Wins = new Label("0 wins");
        VBox player1Box = new VBox(redCircle, p1Name, p1Wins);
        player1Box.setAlignment(Pos.CENTER);
        player1Box.setSpacing(10);
        player1Box.setPadding(new Insets(10));
        player1Box.setStyle("-fx-background-color: white;");

        // Player 2 VBox
        Circle yellowCircle = new Circle(30, Color.YELLOW);
        Label p2Name = new Label("jimmy_john21");
        Label p2Wins = new Label("0 wins");
        VBox player2Box = new VBox(yellowCircle, p2Name, p2Wins);
        player2Box.setAlignment(Pos.CENTER);
        player2Box.setSpacing(10);
        player2Box.setPadding(new Insets(10));
        player2Box.setStyle("-fx-background-color: white;");

        // Turn label
        Label turnLabel = new Label("John_doe_224’s turn...");
        turnLabel.setTextFill(Color.WHITE);

        // Connect 4 board state
        Circle[][] boardCircles = new Circle[ROWS][COLS];
        int[] columnHeights = new int[COLS]; // Tracks number of discs per column
        boolean[] currentPlayer = {true}; // true = red, false = yellow

        // Connect 4 Board Grid
        GridPane boardGrid = new GridPane();
        boardGrid.setHgap(5);
        boardGrid.setVgap(5);
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setStyle("-fx-background-color: navy; -fx-padding: 10;");

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Circle slot = new Circle(25, Color.LIGHTBLUE);
                boardCircles[row][col] = slot;
                boardGrid.add(slot, col, row);
            }
        }

        // Add column click listeners
        for (int col = 0; col < COLS; col++) {
            final int colIndex = col;
            StackPane columnOverlay = new StackPane();
            columnOverlay.setMinSize(55, ROWS * 55);
            columnOverlay.setOnMouseClicked(e -> {
                if (columnHeights[colIndex] < ROWS) {
                    int rowToPlace = ROWS - 1 - columnHeights[colIndex];
                    Circle slot = boardCircles[rowToPlace][colIndex];
                    slot.setFill(currentPlayer[0] ? Color.RED : Color.YELLOW);

                    columnHeights[colIndex]++;
                    round.set(round.get() + 1);

                    // Toggle player
                    currentPlayer[0] = !currentPlayer[0];
                    turnLabel.setText(currentPlayer[0] ? "John_doe_224’s turn..." : "jimmy_john21’s turn...");
                }
            });
            boardGrid.add(columnOverlay, col, 0, 1, ROWS);
        }

        // Chat area
        TextArea chatArea = new TextArea("John_doe_224 : you suck");
        chatArea.setPrefHeight(50);
        chatArea.setEditable(false);

        TextField chatInput = new TextField();
        chatInput.setPromptText("Enter message");
        Button sendBtn = new Button("Send");

        HBox chatInputRow = new HBox(chatInput, sendBtn);
        chatInputRow.setSpacing(5);
        chatInputRow.setAlignment(Pos.CENTER_LEFT);

        VBox chatBox = new VBox(chatArea, chatInputRow);
        chatBox.setSpacing(5);

        // Bottom buttons
        Button quitGame = new Button("Quit Game");
        Button reset = new Button("Reset");
        HBox bottomButtons = new HBox(quitGame, reset);
        bottomButtons.setSpacing(20);
        bottomButtons.setAlignment(Pos.CENTER);

        // Reset logic
        reset.setOnAction(e -> {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    boardCircles[row][col].setFill(Color.LIGHTBLUE);
                }
            }
            for (int i = 0; i < COLS; i++) columnHeights[i] = 0;
            currentPlayer[0] = true;
            round.set(1);
            turnLabel.setText("John_doe_224’s turn...");
        });

        quitGame.setOnAction(e -> sceneManager.showMainMenu());

        // Layout setup
        VBox leftCol = new VBox(player1Box);
        VBox rightCol = new VBox(player2Box);
        VBox centerCol = new VBox(boardGrid, turnLabel);
        centerCol.setSpacing(10);
        centerCol.setAlignment(Pos.CENTER);

        HBox boardSection = new HBox(leftCol, centerCol, rightCol);
        boardSection.setSpacing(40);
        boardSection.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(roundLabel, boardSection, chatBox, bottomButtons);
        mainLayout.setSpacing(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #3a9bdc, #0d58a6);");

        return new Scene(mainLayout, 800, 600);
    }
}



