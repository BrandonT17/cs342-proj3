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

        // Player 1 UI
        Circle redCircle = new Circle(30, Color.RED);
        Label p1Name = new Label(sceneManager.getUsername()); // Show actual username
        Label p1Wins = new Label("0 wins");
        VBox player1Box = new VBox(redCircle, p1Name, p1Wins);
        player1Box.setAlignment(Pos.CENTER);
        player1Box.setSpacing(10);
        player1Box.setPadding(new Insets(10));
        player1Box.setStyle("-fx-background-color: white;");
        player1Box.setMinWidth(100);

        // Player 2 UI
        Circle yellowCircle = new Circle(30, Color.YELLOW);
        Label p2Name = new Label("Waiting for opponent...");
        Label p2Wins = new Label("0 wins");
        VBox player2Box = new VBox(yellowCircle, p2Name, p2Wins);
        player2Box.setAlignment(Pos.CENTER);
        player2Box.setSpacing(10);
        player2Box.setPadding(new Insets(10));
        player2Box.setStyle("-fx-background-color: white;");
        player2Box.setMinWidth(100);

        // Game state variables
        int[] columnHeights = new int[7];
        int[][] boardState = new int[6][7];
        int[] currentPlayer = {1};
        int[] round = {1};
        boolean[] gameOver = {false};
        boolean[] myTurn = {false};

        // Game board setup
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

        Label turnLabel = new Label("Waiting for game to start...");
        turnLabel.setTextFill(Color.WHITE);

        // Chat components
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

        // Game control buttons
        Button quitGame = new Button("Quit Game");
        Button reset = new Button("Reset");
        HBox bottomButtons = new HBox(quitGame, reset);
        bottomButtons.setSpacing(20);
        bottomButtons.setAlignment(Pos.CENTER);

        // Layout organization
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

        // Column click handlers
        for (int col = 0; col < 7; col++) {
            int currentCol = col;
            Rectangle clickableArea = new Rectangle(50, 300);
            clickableArea.setFill(Color.TRANSPARENT);
            clickableArea.setOnMouseClicked((MouseEvent e) -> {
                if (!myTurn[0] || gameOver[0] || columnHeights[currentCol] >= 6) {
                    System.out.println("[CLIENT] Move blocked - Turn:" + myTurn[0] + 
                                    " GameOver:" + gameOver[0] + 
                                    " Column:" + columnHeights[currentCol]);
                    return;
                }

                try {
                    System.out.println("[CLIENT] Sending move for column " + currentCol);
                    Message moveMsg = new Message(MessageType.MOVE, 
                                                String.valueOf(currentCol), 
                                                sceneManager.getUsername());
                    sceneManager.getClient().sendMessage(moveMsg);
                    myTurn[0] = false;
                    turnLabel.setText("Waiting for opponent...");
                } catch (IOException ex) {
                    System.out.println("[CLIENT] Error sending move: " + ex.getMessage());
                    Platform.runLater(() -> 
                        turnLabel.setText("Error sending move to server"));
                }
            });
            boardGrid.add(clickableArea, col, 0, 1, 6);
        }

        // Chat message handler
        sendBtn.setOnAction(e -> {
            String text = chatInput.getText().trim();
            if (!text.isEmpty()) {
                try {
                    Message chatMsg = new Message(MessageType.CHAT, text, sceneManager.getUsername());
                    sceneManager.getClient().sendMessage(chatMsg);
                    chatInput.clear();
                } catch (IOException ex) {
                    System.out.println("[CLIENT] Error sending chat: " + ex.getMessage());
                }
            }
        });

        // Message listener thread
        new Thread(() -> {
            try {
                while (true) {
                    Message msg = sceneManager.getClient().readMessage();
                    Platform.runLater(() -> {
                        System.out.println("[CLIENT] Received: " + msg.getType());
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
                            }
                            case YOUR_TURN -> {
                                myTurn[0] = true;
                                System.out.println("[CLIENT] It's now my turn!");
                                turnLabel.setText("Your turn - Click a column");
                            }
                            case WAIT -> {
                                myTurn[0] = false;
                                turnLabel.setText("Waiting for opponent...");
                            }
                            case GAME_END -> {
                                gameOver[0] = true;
                                turnLabel.setText(msg.getMessage());
                                sceneManager.showGameEnd();
                            }
                            case CHAT -> {
                                chatArea.appendText(msg.getSender() + ": " + msg.getMessage() + "\n");
                            }
                            case GAME_START -> {
                                // Update opponent's name when game starts
                                if (msg.getMessage().contains("vs")) {
                                    String[] players = msg.getMessage().split(" vs ");
                                    p2Name.setText(players[1]);
                                }
                                turnLabel.setText("Game started - waiting for turn");
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    turnLabel.setText("Disconnected from server");
                    System.err.println("[CLIENT] Connection error: " + e.getMessage());
                });
            }
        }).start();

        // Reset button handler
        reset.setOnAction(e -> {
            try {
                Message resetMsg = new Message(MessageType.RESET, "", sceneManager.getUsername());
                sceneManager.getClient().sendMessage(resetMsg);
            } catch (IOException ex) {
                System.out.println("[CLIENT] Error sending reset: " + ex.getMessage());
            }
        });

        // Quit button handler
        quitGame.setOnAction(e -> {
            try {
                sceneManager.getClient().sendMessage(
                    new Message(MessageType.DISCONNECT, "", sceneManager.getUsername()));
                sceneManager.showMainMenu();
            } catch (IOException ex) {
                System.out.println("[CLIENT] Error disconnecting: " + ex.getMessage());
                sceneManager.showMainMenu();
            }
        });

        return new Scene(mainLayout, 800, 600);
    }

    private static boolean checkWin(int[][] board, int row, int col, int player) {
        return checkDirection(board, row, col, player, 1, 0) ||  // Horizontal
               checkDirection(board, row, col, player, 0, 1) ||  // Vertical
               checkDirection(board, row, col, player, 1, 1) ||  // Diagonal down
               checkDirection(board, row, col, player, 1, -1);   // Diagonal up
    }

    private static boolean checkDirection(int[][] board, int row, int col, int player, int dr, int dc) {
        int count = 1;
        // Check in positive direction
        for (int r = row + dr, c = col + dc; 
             r >= 0 && r < 6 && c >= 0 && c < 7 && board[r][c] == player; 
             r += dr, c += dc) {
            count++;
        }
        // Check in negative direction
        for (int r = row - dr, c = col - dc; 
             r >= 0 && r < 6 && c >= 0 && c < 7 && board[r][c] == player; 
             r -= dr, c -= dc) {
            count++;
        }
        return count >= 4;
    }
}
