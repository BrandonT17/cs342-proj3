package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
import com.example.javafxapp.Message;
import com.example.javafxapp.MessageType;
import com.example.javafxapp.network.NetworkClient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.io.IOException;

public class GameplayScene {
    public static Scene create(SceneManager sceneManager) {
        Label roundLabel = new Label("ROUND 1");
        roundLabel.setFont(new Font(24));
        roundLabel.setTextFill(Color.WHITE);

        Circle redCircle = new Circle(30, Color.RED);
        Label p1Name = new Label("player 1");
        Label p1Wins = new Label("0 wins");
        VBox player1Box = new VBox(redCircle, p1Name, p1Wins);
        player1Box.setAlignment(Pos.CENTER);
        player1Box.setSpacing(10);
        player1Box.setPadding(new Insets(10));
        player1Box.setStyle("-fx-background-color: white;");
        player1Box.setMinWidth(100);

        Circle yellowCircle = new Circle(30, Color.YELLOW);
        Label p2Name = new Label("player 2");
        Label p2Wins = new Label("0 wins");
        VBox player2Box = new VBox(yellowCircle, p2Name, p2Wins);
        player2Box.setAlignment(Pos.CENTER);
        player2Box.setSpacing(10);
        player2Box.setPadding(new Insets(10));
        player2Box.setStyle("-fx-background-color: white;");
        player2Box.setMinWidth(100);

        int[] columnHeights = new int[7];
        int[][] boardState = new int[6][7];
        int[] currentPlayer = {1};
        int[] round = {1};
        boolean[] gameOver = {false};
        boolean[] myTurn = {false};

        GridPane boardGrid = new GridPane();
        boardGrid.setHgap(5);
        boardGrid.setVgap(5);
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setStyle("-fx-background-color: navy; -fx-padding: 10;");

        Circle[][] circleNodes = new Circle[6][7];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle slot = new Circle(25, Color.LIGHTBLUE);
                circleNodes[row][col] = slot;
                boardGrid.add(slot, col, row);
            }
        }

        Label turnLabel = new Label("Waiting for your turn...");
        turnLabel.setTextFill(Color.WHITE);

        TextArea chatArea = new TextArea();
        chatArea.setPromptText("Chats will display here");
        chatArea.setPrefHeight(50);
        chatArea.setEditable(false);
        chatArea.setMaxWidth(400);

        TextField chatInput = new TextField();
        chatInput.setPromptText("Enter message");
        chatInput.setPrefWidth(350);

        Button sendBtn = new Button("Send");
        HBox chatInputRow = new HBox(chatInput, sendBtn);
        chatInputRow.setSpacing(5);
        chatInputRow.setAlignment(Pos.CENTER);

        VBox chatBox = new VBox(chatArea, chatInputRow);
        chatBox.setSpacing(5);
        chatBox.setAlignment(Pos.CENTER);

        Button quitGame = new Button("Quit Game");
        Button reset = new Button("Reset");
        HBox bottomButtons = new HBox(quitGame, reset);
        bottomButtons.setSpacing(20);
        bottomButtons.setAlignment(Pos.CENTER);

        VBox leftCol = new VBox(player1Box);
        leftCol.setAlignment(Pos.CENTER);
        VBox rightCol = new VBox(player2Box);
        rightCol.setAlignment(Pos.CENTER);
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

        for (int col = 0; col < 7; col++) {
            int currentCol = col;
            Rectangle clickableArea = new Rectangle(50, 300);
            clickableArea.setFill(Color.TRANSPARENT);
            clickableArea.setOnMouseClicked((MouseEvent e) -> {
                if (!myTurn[0] || gameOver[0] || columnHeights[currentCol] >= 6) return;

                try {
                    Message moveMsg = new Message(MessageType.MOVE, String.valueOf(currentCol), sceneManager.getUsername());
                    sceneManager.getClient().sendMessage(moveMsg);
                    myTurn[0] = false;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            boardGrid.add(clickableArea, col, 0, 1, 6);
        }

        sendBtn.setOnAction(e -> {
            String text = chatInput.getText().trim();
            if (!text.isEmpty()) {
                try {
                    Message chatMsg = new Message(MessageType.CHAT, text, sceneManager.getUsername());
                    sceneManager.getClient().sendMessage(chatMsg);
                    chatInput.clear();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        new Thread(() -> {
            try {
                while (true) {
                    Message msg = sceneManager.getClient().readMessage();
                    Platform.runLater(() -> {
                        switch (msg.getType()) {
                            case MOVE_VALID -> {
                                String[] parts = msg.getMessage().split(",");
                                int player = Integer.parseInt(parts[0]);
                                int col = Integer.parseInt(parts[1]);
                                int row = 5 - columnHeights[col];
                                columnHeights[col]++;
                                boardState[row][col] = player;
                                circleNodes[row][col].setFill(player == 1 ? Color.RED : Color.YELLOW);
                                currentPlayer[0] = player == 1 ? 2 : 1;
                                round[0]++;
                                roundLabel.setText("ROUND " + round[0]);
                                turnLabel.setText(currentPlayer[0] == 1 ? "Your turn" : "Opponent's turn");
                            }
                            case YOUR_TURN -> {
                                myTurn[0] = true;
                                turnLabel.setText("Your turn");
                            }
                            case WAIT -> {
                                myTurn[0] = false;
                                turnLabel.setText("Waiting for opponent...");
                            }
                            case GAME_END -> {
                                gameOver[0] = true;
                                turnLabel.setText(msg.getMessage());
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> turnLabel.setText("Disconnected from server."));
            }
        }).start();

        reset.setOnAction(e -> {
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 7; c++) {
                    boardState[r][c] = 0;
                    circleNodes[r][c].setFill(Color.LIGHTBLUE);
                }
            }
            for (int i = 0; i < 7; i++) columnHeights[i] = 0;
            round[0] = 1;
            gameOver[0] = false;
            currentPlayer[0] = 1;
            roundLabel.setText("ROUND 1");
            turnLabel.setText("Waiting for your turn...");
        });

        quitGame.setOnAction(e -> sceneManager.showMainMenu());

        return new Scene(mainLayout, 800, 600);
    }

    private static boolean checkWin(int[][] board, int row, int col, int player) {
        return checkDirection(board, row, col, player, 1, 0) ||
               checkDirection(board, row, col, player, 0, 1) ||
               checkDirection(board, row, col, player, 1, 1) ||
               checkDirection(board, row, col, player, 1, -1);
    }

    private static boolean checkDirection(int[][] board, int row, int col, int player, int dr, int dc) {
        int count = 1;
        int r = row + dr, c = col + dc;
        while (r >= 0 && r < 6 && c >= 0 && c < 7 && board[r][c] == player) {
            count++;
            r += dr;
            c += dc;
        }
        r = row - dr;
        c = col - dc;
        while (r >= 0 && r < 6 && c >= 0 && c < 7 && board[r][c] == player) {
            count++;
            r -= dr;
            c -= dc;
        }
        return count >= 4;
    }
}
