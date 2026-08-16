import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ScorePanel extends JPanel {
    private JLabel playerLabel, computerLabel, drawLabel;

    public ScorePanel() {
        setLayout(new GridLayout(3, 1, 5, 10));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Score", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.BLACK));

        playerLabel = new JLabel("Player Wins: 0", JLabel.CENTER);
        computerLabel = new JLabel("Computer Wins: 0", JLabel.CENTER);
        drawLabel = new JLabel("Draws: 0", JLabel.CENTER);

        add(playerLabel);
        add(computerLabel);
        add(drawLabel);
    }

    public void update(int pWins, int cWins, int draws) {
        playerLabel.setText("Player Wins: " + pWins);
        computerLabel.setText("Computer Wins: " + cWins);
        drawLabel.setText("Draws: " + draws);
    }
}