import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientNetworkHandler {
	private final GameController controller;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	public ClientNetworkHandler(GameController controller) {
		this.controller = controller;

		try {
			socket = new Socket("localhost", 8000);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			startListening();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Please start the Tic-Tac-Toe Server first, then restart this client.",
					"Connection Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	public void send(String msg) {
		if (out == null)
			return;
		try {
			out.writeUTF(msg);
			out.flush();
		} catch (IOException e) {
			System.exit(0);
		}
	}

	private void startListening() {
		new Thread(() -> {
			try {
				while (true) {
					String msg = in.readUTF();
					controller.handleServerMessage(msg);
				}
			} catch (IOException e) {
				System.exit(0);
			}
		}).start();
	}
}