import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TicTacToeGame extends JFrame implements ActionListener {
	private JTextField nameField;
	private JButton submitButton;
	private JLabel messageLabel, timeLabel;
	private BoardButton[][] buttons = new BoardButton[3][3];
	private ScorePanel scorePanel;

	private String playerName = "";
	private char currentTurn = 'X';
	private boolean nameSubmitted = false;

	private Timer clockTimer, computerTimer;
	private final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");

	private ImageIcon xMark, oMark;
	private GameLogic logic = new GameLogic();
	private ComputerPlayer ai = new ComputerPlayer();

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignored) {
			}
			new TicTacToeGame().setVisible(true);
		});
	}

	public TicTacToeGame() {
		setTitle("Tic Tac Toe");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		loadImages();
		initComponents();
		startClock();

		setSize(800, 700);
		setLocationRelativeTo(null);
	}

	private void loadImages() {
//		try {
		Image xPng = new ImageIcon("res/x.png").getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
		Image oPng = new ImageIcon("res/o.png").getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
		xMark = new ImageIcon(xPng);
		oMark = new ImageIcon(oPng);
//		} catch (Exception e) {
//			xMark = oMark = null;
//		}
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		createMenuBar();
		createNorthPanel();
		createCenterBoard();
		createSouthPanel();

		scorePanel = new ScorePanel();
		add(scorePanel, BorderLayout.EAST);
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu control = new JMenu("Control");
		JMenu help = new JMenu("Help");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(e -> System.exit(0));
		control.add(exit);
		
		JMenuItem instruction = new JMenuItem("Instruction");
		instruction.addActionListener(e -> showInstructions());
		help.add(instruction);
		menuBar.add(control);
		menuBar.add(help);
		setJMenuBar(menuBar);
	}

	private void createNorthPanel() {
		JPanel north = new JPanel(new BorderLayout());
		north.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		messageLabel = new JLabel("Enter your player name...", JLabel.CENTER);
		messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		north.add(messageLabel, BorderLayout.CENTER);

		add(north, BorderLayout.NORTH);
	}

	private void createSouthPanel() {
		JPanel south = new JPanel(new BorderLayout(10, 5));
		south.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

		JPanel inputLine = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
		nameField = new JTextField(15);
		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		inputLine.add(new JLabel("Enter your name:"));
		inputLine.add(nameField);
		inputLine.add(submitButton);
		south.add(inputLine, BorderLayout.CENTER);

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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton) {
			submitName();
		} else if (nameSubmitted) {
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					if (e.getSource() == buttons[i][j]) {
						playerMove(i, j);
						return;
					}
		}
	}

	private void submitName() {
		String name = nameField.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter a name!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		playerName = name;
		nameSubmitted = true;
		nameField.setEnabled(false);
		submitButton.setEnabled(false);
		setTitle("Tic Tac Toe - Player: " + playerName);
		messageLabel.setText("WELCOME " + playerName.toUpperCase());

		for (BoardButton[] row : buttons)
			for (BoardButton b : row)
				b.setEnabled(true);
	}

	private void playerMove(int r, int c) {
		if (currentTurn != 'X' || !logic.makeMove(r, c, 'X'))
			return;

		setIcon(buttons[r][c], 'X');
		if (logic.checkWin('X'))
			endRound("Player wins!");
		else if (logic.isFull())
			endRound("It's a draw!");
		else {
			currentTurn = 'O';
			messageLabel.setText("Vaild move, wait for your opponent.");
			startComputerDelay();
		}
	}

	private void startComputerDelay() {
		computerTimer = new Timer(2000, ev -> {
			computerMove();
			computerTimer.stop();
		});
		computerTimer.setRepeats(false);
		computerTimer.start();
	}

	private void computerMove() {
		char[][] board = new char[3][3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				board[i][j] = logic.getCell(i, j);

		int[] position = ai.getMove(board);
		if (position != null) {
			logic.makeMove(position[0], position[1], 'O');
			setIcon(buttons[position[0]][position[1]], 'O');
		}

		if (logic.checkWin('O'))
			endRound("Computer wins!");
		else if (logic.isFull())
			endRound("It's a draw!");
		else {
			currentTurn = 'X';
			messageLabel.setText("Your opponent has moved, now is your turn.");
		}
	}

	private void setIcon(BoardButton button, char mark) {
		if (mark == 'X' && xMark != null)
			button.setIcon(xMark);
		else if (mark == 'O' && oMark != null)
			button.setIcon(oMark);
		else {
			button.setIcon(null);
			button.setText(String.valueOf(mark));
		}
	}

	private void updateBoard() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				char cell = logic.getCell(i, j);
				buttons[i][j].setIcon(null);
				buttons[i][j].setText(cell == 0 ? "" : String.valueOf(cell));
				if (cell != 0)
					setIcon(buttons[i][j], cell);
			}
	}

	private void endRound(String result) {
		if (result.contains("Player"))
			logic.incrementPlayerWin();
		else if (result.contains("Computer"))
			logic.incrementComputerWin();
		else
			logic.incrementDraw();

		scorePanel.update(logic.getPlayerWins(), logic.getComputerWins(), logic.getDraws());

		int optionPanel = JOptionPane.showOptionDialog(this, result, "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Yes"}, "Yes");

		if (optionPanel == 0) {
			logic.reset();
			updateBoard();
			currentTurn = 'X';
			messageLabel.setText("WELCOME " + playerName.toUpperCase());
		} else {
			System.exit(0);
		}
	}

	private void showInstructions() {
		String rules = """
				                Some information about the game:
				                - The move is not occupied by any mark.
				                - The move is made in the player's turn.
				                - The move is made within the 3 x 3 board.
				                The game would continue and switch amoung the player and the computer until it reaches either one of the following conditions:
				                - Player wins.
				                - Computer wins.
				                - Draw.
				        """;
		JOptionPane.showOptionDialog(this, rules, "Instructions", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "Yes" }, "Yes");
	}
}