import java.awt.BorderLayout;

import javax.swing.*;

public class GameController {
	private final GameFrame view;
	private final ClientNetworkHandler network;
	private int myPlayer;
	private char myMark;
	private boolean isMyTurn;
	private String playerName = "";
	private JDialog gameOverDialog;

	public GameController(GameFrame view) {
		this.view = view;
		this.network = new ClientNetworkHandler(this);
	}

	public void handleServerMessage(String msg) {
		if (msg.startsWith("YOU_ARE ")) {
			myPlayer = Integer.parseInt(msg.substring(8));
			if (myPlayer == 1)
				myMark = 'X';
			else
				myMark = 'O';
			getView().setMessage("Enter your player name...");
		} else if (msg.startsWith("PLACE ")) {
			String[] p = msg.substring(6).split(" ");
			int r = Integer.parseInt(p[0]), c = Integer.parseInt(p[1]);
			char mark = p[2].charAt(0);
			getView().placeMark(r, c, mark);
			if (mark != myMark) {
				getView().setMessage("Your opponent has moved. Now is your turn.");
				isMyTurn = true;
				getView().enableEmptyCells();
			}
		} else if (msg.startsWith("WIN ")) {
			int winner = Integer.parseInt(msg.substring(4));
			showGameOver("Player " + winner + " wins!");
		} else if (msg.equals("DRAW")) {
			showGameOver("It's a draw!");
		} else if (msg.startsWith("SCORES ")) {
			String[] p = msg.substring(7).split(" ");
			getView().updateScore(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
		} else if (msg.equals("NEW_GAME")) {
			getView().clearBoard();
			getView().setMessage("WELCOME " + playerName.toUpperCase());
			isMyTurn = (myPlayer == 1);
			if (isMyTurn)
				getView().enableEmptyCells();
		} else if (msg.equals("OPPONENT_LEFT")) {
			if (gameOverDialog != null && gameOverDialog.isShowing()) {
				gameOverDialog.dispose();
			}
			JOptionPane.showMessageDialog(getView(), "Game Ends. One of the players left.", "Game Over",
					JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
	}

	public void submitName(String name) {
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(getView(), "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		playerName = name;
		getView().nameSubmitted(name);
		network.send("NAME " + name);
		if (myPlayer == 1) {
			isMyTurn = true;
			getView().enableEmptyCells();
		}
	}

	public void attemptMove(int r, int c) {
		if (!isMyTurn)
			return;
		network.send("MOVE " + r + " " + c);
		getView().setMessage("Valid move, wait for your opponent.");
		getView().disableAllCells();
		isMyTurn = false;
	}

	private void showGameOver(String result) {
		SwingUtilities.invokeLater(() -> {
			if (gameOverDialog != null && gameOverDialog.isShowing())
				return;
			gameOverDialog = new JDialog(getView(), "Game Over", true);
			gameOverDialog.setLayout(new BorderLayout());
			gameOverDialog.setSize(300, 150);
			gameOverDialog.setLocationRelativeTo(getView());

			String msg = "";
			if (result.contains("Player " + myPlayer))
				msg = "Congratulations! You win!\nDo you want to play again?";
			else if (!result.contains("draw"))
				msg = "You lose.\nDo you want to play again?";
			else
				msg = "It's a draw!\nPlay again?";

			JLabel label = new JLabel("<html><div style='text-align: center;'>" + msg + "</div></html>");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			gameOverDialog.add(label, BorderLayout.CENTER);

			JPanel buttons = new JPanel();
			JButton yes = new JButton("Yes");
			yes.addActionListener(e -> {
				network.send("RESTART");
				gameOverDialog.dispose();
			});
			JButton no = new JButton("No");
			no.addActionListener(e -> {
				network.send("EXIT");
				gameOverDialog.dispose();
				System.exit(0);
			});
			buttons.add(yes);
			buttons.add(no);
			gameOverDialog.add(buttons, BorderLayout.SOUTH);

			gameOverDialog.setVisible(true);
		});
	}

	public void exit() {
		network.send("EXIT");
		System.exit(0);
	}

	public GameFrame getView() {
		return view;
	}
}