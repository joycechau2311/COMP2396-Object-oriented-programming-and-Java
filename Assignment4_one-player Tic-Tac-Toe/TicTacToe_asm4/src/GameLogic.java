public class GameLogic {
    private char[][] board = new char[3][3];
    private int playerWins = 0, computerWins = 0, draws = 0;

    public void reset() {
        for (int i = 0; i < 3; i++)
            java.util.Arrays.fill(board[i], (char)0);
    }

    public boolean makeMove(int r, int c, char player) {
        if (board[r][c] != 0) 
        	return false;
        board[r][c] = player;
        return true;
    }

    public boolean checkWin(char p) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == p && board[i][1] == p && board[i][2] == p) 
            	return true;
            if (board[0][i] == p && board[1][i] == p && board[2][i] == p) 
            	return true;
        }
        return (board[0][0] == p && board[1][1] == p && board[2][2] == p) || (board[0][2] == p && board[1][1] == p && board[2][0] == p);
    }

    public boolean isFull() {
        for (char[] row : board)
            for (char cell : row)
                if (cell == 0) 
                	return false;
        return true;
    }

    public void incrementPlayerWin() { playerWins++; }
    public void incrementComputerWin() { computerWins++; }
    public void incrementDraw() { draws++; }

    public int getPlayerWins() { return playerWins; }
    public int getComputerWins() { return computerWins; }
    public int getDraws() { return draws; }

    public char getCell(int r, int c) { return board[r][c]; }
}