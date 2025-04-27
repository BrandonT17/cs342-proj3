package com.example.javafxapp;

public class GameLogic {
    private int[][] board;
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private int lastMoveRow = -1;
    private int lastMoveCol = -1;

    public GameLogic() {
        board = new int[ROWS][COLS];
        resetBoard();
    }

    public boolean makeMove(int column, int player) {
        if (column < 0 || column >= COLS) return false;

        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                board[row][column] = player;
                lastMoveRow = row;
                lastMoveCol = column;
                return true;
            }
        }
        return false;
    }

    public boolean checkWin() {
        if (lastMoveRow == -1 || lastMoveCol == -1) return false;
        int player = board[lastMoveRow][lastMoveCol];
        return checkHorizontal() || checkVertical() || checkDiagonal();
    }

    public boolean checkWin(int player) {
        return checkWin();
    }

    public boolean isDraw() {
        return isBoardFull();
    }

    private boolean checkHorizontal() {
        int player = board[lastMoveRow][lastMoveCol];
        int count = 0;
        for (int c = 0; c < COLS; c++) {
            if (board[lastMoveRow][c] == player) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }
        return false;
    }

    private boolean checkVertical() {
        int player = board[lastMoveRow][lastMoveCol];
        int count = 0;
        for (int r = 0; r < ROWS; r++) {
            if (board[r][lastMoveCol] == player) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }
        return false;
    }

    private boolean checkDiagonal() {
        return checkDiagonalDown() || checkDiagonalUp();
    }

    private boolean checkDiagonalDown() {
        int player = board[lastMoveRow][lastMoveCol];
        int count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = lastMoveRow + i;
            int c = lastMoveCol + i;
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

    private boolean checkDiagonalUp() {
        int player = board[lastMoveRow][lastMoveCol];
        int count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = lastMoveRow - i;
            int c = lastMoveCol + i;
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
        lastMoveRow = -1;
        lastMoveCol = -1;
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

    public int getLastMoveRow() {
        return lastMoveRow;
    }

    public int getLastMoveCol() {
        return lastMoveCol;
    }
}

