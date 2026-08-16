import javax.swing.*;

public class TicTacToeGame {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignored) {
			}
			new GameFrame().setVisible(true);
		});
	}
}