package com.example.javafxapp.scenes;

import com.example.javafxapp.SceneManager;
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

public class GameplayScene {
    public static Scene create(SceneManager sceneManager) {
        // display current round
        Label roundLabel = new Label("ROUND 1");
            roundLabel.setFont(new Font(24));
            roundLabel.setTextFill(Color.WHITE);

        // player 1 HUD
        Circle redCircle = new Circle(30, Color.RED);
        Label p1Name = new Label("player 1");
        Label p1Wins = new Label("0 wins");
        VBox player1Box = new VBox(redCircle, p1Name, p1Wins);
            player1Box.setAlignment(Pos.CENTER);
            player1Box.setSpacing(10);
            player1Box.setPadding(new Insets(10));
            player1Box.setStyle("-fx-background-color: white;");
            player1Box.setMinWidth(100);

        // player 2 HUD
        Circle yellowCircle = new Circle(30, Color.YELLOW);
        Label p2Name = new Label("player 2");
        Label p2Wins = new Label("0 wins");
        VBox player2Box = new VBox(yellowCircle, p2Name, p2Wins);
            player2Box.setAlignment(Pos.CENTER);
            player2Box.setSpacing(10);
            player2Box.setPadding(new Insets(10));
            player2Box.setStyle("-fx-background-color: white;");
            player2Box.setMinWidth(100);

        // game state
        int[] columnHeights = new int[7];
        int[][] boardState = new int[6][7];
        int[] currentPlayer = {1};
        int[] round = {1};
        boolean[] gameOver = {false};

        // board grid using gridpane, board background
        GridPane boardGrid = new GridPane();
            boardGrid.setHgap(5);
            boardGrid.setVgap(5);
            boardGrid.setAlignment(Pos.CENTER);
            boardGrid.setStyle("-fx-background-color: navy; -fx-padding: 10;");
        
        // initialize empty spots
        Circle[][] circleNodes = new Circle[6][7];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle slot = new Circle(25, Color.LIGHTBLUE);
                circleNodes[row][col] = slot;
                boardGrid.add(slot, col, row);
            }
        }

        // display user turn
        // TODO: display actual usernames using play1username and player2username variables
        Label turnLabel = new Label("John_doe_224’s turn...");
            turnLabel.setTextFill(Color.WHITE);

        // user chat
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

        // buttons
        Button quitGame = new Button("Quit Game");
        Button reset = new Button("Reset");
        HBox bottomButtons = new HBox(quitGame, reset);
            bottomButtons.setSpacing(20);
            bottomButtons.setAlignment(Pos.CENTER);

        // layout
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

        // user interaction
        for (int col = 0; col < 7; col++) {
            int currentCol = col;
            Rectangle clickableArea = new Rectangle(50, 300);
            clickableArea.setFill(Color.TRANSPARENT);
            clickableArea.setOnMouseClicked((MouseEvent e) -> {
                if (gameOver[0] || columnHeights[currentCol] >= 6) return;

                int row = 5 - columnHeights[currentCol];
                columnHeights[currentCol]++;
                boardState[row][currentCol] = currentPlayer[0];
                circleNodes[row][currentCol].setFill(currentPlayer[0] == 1 ? Color.RED : Color.YELLOW);

                if (checkWin(boardState, row, currentCol, currentPlayer[0])) {
                    turnLabel.setText((currentPlayer[0] == 1 ? "John_doe_224" : "jimmy_john21") + " wins!");
                    gameOver[0] = true;
                    sceneManager.showGameEnd();
                    return;
                }

                // check for draw
                boolean draw = true;
                for (int i = 0; i < 7; i++) {
                    if (columnHeights[i] < 6) {
                        draw = false;
                        break;
                    }
                }
                if (draw) {
                    turnLabel.setText("It's a draw!");
                    gameOver[0] = true;
                    return;
                }

                // switch player turn
                currentPlayer[0] = currentPlayer[0] == 1 ? 2 : 1;
                turnLabel.setText(currentPlayer[0] == 1 ? "John_doe_224’s turn..." : "jimmy_john21’s turn...");

                // increment round
                round[0]++;
                roundLabel.setText("ROUND " + round[0]);
            });

            boardGrid.add(clickableArea, col, 0, 1, 6);
        }

        // reset board
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
            turnLabel.setText("John_doe_224’s turn...");
        });

        quitGame.setOnAction(e -> sceneManager.showMainMenu());

        return new Scene(mainLayout, 800, 600);
    }

    private static boolean checkWin(int[][] board, int row, int col, int player) {
        return checkDirection(board, row, col, player, 1, 0) ||  // Horizontal
               checkDirection(board, row, col, player, 0, 1) ||  // Vertical
               checkDirection(board, row, col, player, 1, 1) ||  // Diagonal \
               checkDirection(board, row, col, player, 1, -1);   // Diagonal /
    }

    private static boolean checkDirection(int[][] board, int row, int col, int player, int dr, int dc) {

        int count = 1;
        int r = row + dr, c = col + dc;
        while (r >= 0 && r < 6 && c >= 0 && c < 7 && board[r][c] == player) {
            count++;
            r += dr;
            c += dc;
        }
        // count backwards
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

