import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ScorePanel extends JPanel {
	private JLabel player1Label, player2Label, drawLabel;

	public ScorePanel() {
		setLayout(new GridLayout(3, 1, 5, 10));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Score", TitledBorder.CENTER,
				TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.BLACK));

		player1Label = new JLabel("Player 1 Wins: 0", JLabel.CENTER);
		player2Label = new JLabel("Player 2 Wins: 0", JLabel.CENTER);
		drawLabel = new JLabel("Draws: 0", JLabel.CENTER);

		add(player1Label);
		add(player2Label);
		add(drawLabel);
	}

	public void update(int p1Wins, int p2Wins, int draws) {
		player1Label.setText("Player 1 Wins: " + p1Wins);
		player2Label.setText("Player 2 Wins: " + p2Wins);
		drawLabel.setText("Draws: " + draws);
	}
}