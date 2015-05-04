import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

class ServerHandler extends Thread {
	private String username;
	private Socket socket;
	private BufferedReader sIn;
	private PrintWriter sOut;
	private ArrayList<String> messageQueue = new ArrayList<String>();
	private ArrayList<PrintWriter> serverOut;
	private ArrayList<String> usernames;
	private boolean hasUsername = false;

	public ServerHandler(Socket socket, ArrayList<PrintWriter> writers,
			ArrayList<String> names) {
		this.socket = socket;
		this.serverOut = writers;
		this.usernames = names;
	}

	public synchronized void addToMsgQueue(String new_message) {
		messageQueue.add(new_message);
		System.out.println("message added to server message queue");
		notify();
	}

	public synchronized void sendMessage() throws InterruptedException {
		while (messageQueue.size() == 0) {
			System.out.println("waiting on sending message...");
			wait();
		}
		while (messageQueue.size() > 0) {
			String message = messageQueue.remove(0);
			System.out.println("Sever msgQ: " + message);
			for (PrintWriter toClient : serverOut) {
				toClient.println("MESSAGE " + username + ": " + message);
			}
		}
	}

	public void setUsername() throws IOException {
		while (!hasUsername) {
			sOut.println("USERNAME");
			username = sIn.readLine();
			if (username == null) {
				return;
			}
			synchronized (usernames) {
				if (!usernames.contains(username)) {
					usernames.add(username);
					hasUsername = true;
				}
			}
		}
	}

	public void run() {
		try {
			sIn = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			sOut = new PrintWriter(socket.getOutputStream(), true);
			setUsername();

			sOut.println("SOUNDSGOOD");
			serverOut.add(sOut);

			while (true) {
				String msg = sIn.readLine();
				if (msg == null) {
					return;
				}
				addToMsgQueue(msg);
				try {
					sendMessage();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {

			if (username != null) {
				usernames.remove(username);
			}
			if (sOut != null) {
				serverOut.remove(sOut);
			}
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
