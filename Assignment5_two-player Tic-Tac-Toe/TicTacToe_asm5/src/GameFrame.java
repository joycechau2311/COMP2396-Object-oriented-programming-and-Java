import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameFrame extends JFrame implements ActionListener {
	private JLabel messageLabel, timeLabel;
	private JTextField nameField;
	private JButton submitButton;
	private BoardButton[][] buttons = new BoardButton[3][3];
	private ScorePanel scorePanel;
	private GameController controller;
	private ImageIcon xMark, oMark;
	private Timer clockTimer;
	private final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");

	public GameFrame() {
		setTitle("Tic Tac Toe");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (controller != null)
					controller.exit();
				else
					System.exit(0);
			}
		});

		loadImages();
		setupGUI();
		startClock();

		controller = new GameController(this);

		setSize(550, 550);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void loadImages() {
		try {
			Image xPng = new ImageIcon("res/x.png").getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
			Image oPng = new ImageIcon("res/o.png").getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
			xMark = new ImageIcon(xPng);
			oMark = new ImageIcon(oPng);
		} catch (Exception e) {
			xMark = oMark = null;
		}
	}

	private void setupGUI() {
		setLayout(new BorderLayout());
		createMenuBar();
		createNorthPanel();
		createCenterBoard();
		createSouthPanel();

		scorePanel = new ScorePanel();
		add(scorePanel, BorderLayout.EAST);
	}

	private void createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu control = new JMenu("Control");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(e -> controller.exit());
		control.add(exit);
		JMenu help = new JMenu("Help");
		JMenuItem instruction = new JMenuItem("Instruction");
		instruction.addActionListener(e -> showInstructions());
		help.add(instruction);
		menubar.add(control);
		menubar.add(help);
		setJMenuBar(menubar);
	}

	private void createNorthPanel() {
		JPanel north = new JPanel(new BorderLayout());
		north.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		messageLabel = new JLabel("Enter your player name...", JLabel.CENTER);
		messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		north.add(messageLabel);
		add(north, BorderLayout.NORTH);
	}

	private void createSouthPanel() {
		JPanel south = new JPanel(new BorderLayout());
		south.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

		JPanel input = new JPanel(new FlowLayout());
		nameField = new JTextField(15);
		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		input.add(new JLabel("Enter your name:"));
		input.add(nameField);
		input.add(submitButton);
		south.add(input, BorderLayout.CENTER);

		timeLabel = new JLabel("Current Time: --:--:--", JLabel.CENTER);
		timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		south.add(timeLabel, BorderLayout.SOUTH);
		add(south, BorderLayout.SOUTH);
	}

	private void createCenterBoard() {
		JPanel grid = new JPanel(new GridLayout(3, 3, 5, 5));
		grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				buttons[i][j] = new BoardButton(i, j);
				buttons[i][j].addActionListener(this);
				buttons[i][j].setEnabled(false);
				grid.add(buttons[i][j]);
			}
		add(grid, BorderLayout.CENTER);
	}

	private void startClock() {
		clockTimer = new Timer(1000, e -> timeLabel.setText("Current Time: " + fmt.format(new Date())));
		clockTimer.start();
	}

	private void showInstructions() {
		String rules = """
				Some information about the game:

				Criteria for a valid move:
				- The move is not occupied by any mark.
				- The move is made in the player's turn.
				- The move is made within the 3 x 3 board.
				The game would continue and switch among the opposite player until it reaches either one of the following conditions:
				- Player 1 wins.
				- Player 2 wins.
				- Draw.
				- One of the players leaves the game.
				""";
		JOptionPane.showMessageDialog(this, rules, "Instructions", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton) {
			controller.submitName(nameField.getText().trim());
		} else {
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					if (e.getSource() == buttons[i][j]) {
						controller.attemptMove(i, j);
						return;
					}
		}
	}

	public void setMessage(String msg) {
		messageLabel.setText(msg);
	}

	public void updateScore(int p1, int p2, int draw) {
		scorePanel.update(p1, p2, draw);
	}

	public void clearBoard() {
		for (BoardButton[] row : buttons) {
			for (BoardButton b : row) {
				b.setIcon(null);
				b.setText("");
				b.setEnabled(false);
			}
		}
	}

	public void placeMark(int r, int c, char mark) {
		BoardButton b = buttons[r][c];
		if (mark == 'X' && xMark != null)
			b.setIcon(xMark);
		else if (mark == 'O' && oMark != null)
			b.setIcon(oMark);
		else
			b.setText(String.valueOf(mark));
		b.setEnabled(false);
	}

	public void enableEmptyCells() {
		for (BoardButton[] row : buttons)
			for (BoardButton b : row)
				b.setEnabled(b.getIcon() == null && b.getText().isEmpty());
	}

	public void disableAllCells() {
		for (BoardButton[] row : buttons)
			for (BoardButton b : row)
				b.setEnabled(false);
	}

	public void nameSubmitted(String name) {
		nameField.setEnabled(false);
		submitButton.setEnabled(false);
		setTitle("Tic Tac Toe - Player: " + name);
		setMessage("WELCOME " + name.toUpperCase());
	}
}