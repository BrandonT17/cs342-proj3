package com.example.javafxapp;

public class GameLogic {
    private int[][] board;
    private static final int ROWS = 6;
    private static final int COLS = 7;

    public GameLogic() {
        board = new int[ROWS][COLS];
        resetBoard();
    }

    public boolean makeMove(int column, int player) {
        if (column < 0 || column >= COLS) return false;
        
        // Find the lowest empty row in the column
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                board[row][column] = player;
                return true;
            }
        }
        return false;
    }

    public boolean checkWin(int column, int row, int player) {
        // Check horizontal
        int count = 0;
        for (int c = 0; c < COLS; c++) {
            if (board[row][c] == player) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check vertical
        count = 0;
        for (int r = 0; r < ROWS; r++) {
            if (board[r][column] == player) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check diagonal (both directions)
        return checkDiagonal(row, column, player);
    }

    private boolean checkDiagonal(int row, int col, int player) {
        // Check diagonal (top-left to bottom-right)
        int count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = row + i;
            int c = col + i;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                if (board[r][c] == player) {
                    count++;
                    if (count == 4) return true;
                } else {
                    count = 0;
                }
            }
        }

        // Check diagonal (top-right to bottom-left)
        count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = row + i;
            int c = col - i;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                if (board[r][c] == player) {
                    count++;
                    if (count == 4) return true;
                } else {
                    count = 0;
                }
            }
        }
        return false;
    }

    public boolean isBoardFull() {
        for (int c = 0; c < COLS; c++) {
            if (board[0][c] == 0) return false;
        }
        return true;
    }

    public void resetBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c] = 0;
            }
        }
    }

    public String getBoardState() {
        StringBuilder state = new StringBuilder();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                state.append(board[r][c]);
            }
            state.append("|");
        }
        return state.toString();
    }

    public int[][] getBoardArray() {
        return board;
    }
} 
