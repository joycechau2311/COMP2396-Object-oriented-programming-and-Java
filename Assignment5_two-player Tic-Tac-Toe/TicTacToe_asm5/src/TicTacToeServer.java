import java.io.*;
import java.net.*;

public class TicTacToeServer {
	private char[][] board = new char[3][3];
	private int currentPlayer = 1;
	private int p1Wins = 0, p2Wins = 0, draws = 0;
	private boolean gameOver = false;
	private boolean[] wantRestart = new boolean[2];
	private String[] names = new String[2];

	private Socket socket1, socket2;
	private DataOutputStream to1, to2;
	private DataInputStream from1, from2;

	public static void main(String[] args) {
		new TicTacToeServer();
	}

	public TicTacToeServer() {
		try {
			ServerSocket server = new ServerSocket(8000);
			System.out.println("Server started at " + new java.util.Date());

			socket1 = server.accept();
			to1 = new DataOutputStream(socket1.getOutputStream());
			from1 = new DataInputStream(socket1.getInputStream());
			to1.writeUTF("YOU_ARE 1");

			socket2 = server.accept();
			to2 = new DataOutputStream(socket2.getOutputStream());
			from2 = new DataInputStream(socket2.getInputStream());
			to2.writeUTF("YOU_ARE 2");

			new Thread(() -> handleClient(1)).start();
			new Thread(() -> handleClient(2)).start();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void handleClient(int player) {
		DataInputStream from;
		if (player == 1)
			from = from1;
		else
			from = from2;
		try {
			while (true) {
				String msg = from.readUTF();
				processMessage(msg, player);
			}
		} catch (IOException ex) {
			handleDisconnect(player);
		}
	}

	private synchronized void processMessage(String msg, int player) {
		if (msg.startsWith("NAME ")) {
			names[player - 1] = msg.substring(5);
			sendBoard(to1);
			sendBoard(to2);
			sendScores(to1);
			sendScores(to2);
		} else if (msg.startsWith("MOVE ")) {
			if (gameOver || currentPlayer != player)
				return;
			String[] parts = msg.substring(5).split(" ");
			int r = Integer.parseInt(parts[0]), c = Integer.parseInt(parts[1]);
			if (r < 0 || r > 2 || c < 0 || c > 2 || board[r][c] != 0)
				return;
			char mark;
			if (player == 1)
				mark = 'X';
			else
				mark = 'O';
			board[r][c] = mark;
			sendToBoth("PLACE " + r + " " + c + " " + mark);
			if (checkWin(mark)) {
				gameOver = true;
				sendToBoth("WIN " + player);
				if (player == 1)
					p1Wins++;
				else
					p2Wins++;
				sendScores(to1);
				sendScores(to2);
				wantRestart[0] = wantRestart[1] = false;
			} else if (isFull()) {
				gameOver = true;
				draws++;
				sendToBoth("DRAW");
				sendScores(to1);
				sendScores(to2);
				wantRestart[0] = wantRestart[1] = false;
			} else {
				currentPlayer = 3 - player;
			}
		} else if (msg.equals("RESTART")) {
			if (gameOver) {
				wantRestart[player - 1] = true;
				if (wantRestart[0] && wantRestart[1]) {
					resetBoard();
					sendToBoth("NEW_GAME");
					gameOver = false;
				}
			}
		} else if (msg.equals("EXIT")) {
			handleDisconnect(player);
		}
	}

	private void handleDisconnect(int player) {
		try {
			DataOutputStream other;
			if (player == 1)
				other = to2;
			else
				other = to1;
			if (other != null)
				other.writeUTF("OPPONENT_LEFT");
		} catch (IOException ignored) {
		}
		System.exit(0);
	}

	private void sendToBoth(String msg) {
		try {
			if (to1 != null)
				to1.writeUTF(msg);
		} catch (IOException ignored) {
		}
		try {
			if (to2 != null)
				to2.writeUTF(msg);
		} catch (IOException ignored) {
		}
	}

	private void sendBoard(DataOutputStream to) {
		try {
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					if (board[i][j] != 0)
						to.writeUTF("PLACE " + i + " " + j + " " + board[i][j]);
		} catch (IOException ignored) {
		}
	}

	private void sendScores(DataOutputStream to) {
		try {
			to.writeUTF("SCORES " + p1Wins + " " + p2Wins + " " + draws);
		} catch (IOException ignored) {
		}
	}

	private boolean checkWin(char p) {
		for (int i = 0; i < 3; i++) {
			if (board[i][0] == p && board[i][1] == p && board[i][2] == p)
				return true;
			if (board[0][i] == p && board[1][i] == p && board[2][i] == p)
				return true;
		}
		return (board[0][0] == p && board[1][1] == p && board[2][2] == p)
				|| (board[0][2] == p && board[1][1] == p && board[2][0] == p);
	}

	private boolean isFull() {
		for (char[] row : board)
			for (char cell : row)
				if (cell == 0)
					return false;
		return true;
	}

	private void resetBoard() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				board[i][j] = 0;
		currentPlayer = 1;
	}
}