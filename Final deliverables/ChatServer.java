import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer extends Thread {

	private static final int LISTENER_PORT = 5451;
	private static ArrayList<String> usernames = new ArrayList<String>();
	private static ArrayList<PrintWriter> serverOut = new ArrayList<PrintWriter>();	
	private ServerSocket serverSocket;

	public void stopServer() {
		try {
			System.out.println("closed server");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		
		System.out.println("The chat server is running.");
		try {
			serverSocket = new ServerSocket(LISTENER_PORT);
			serverSocket.setSoTimeout(60000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String address = "";
		try {
			address = GeneralInetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println("Address of Nightosphere: " + address);

		try {
			while (true) {
				System.out.println("accepting clients...");
				ServerHandler sh = new ServerHandler(serverSocket.accept(),
						serverOut, usernames, this);
				if (sh != null) {
					sh.start();
				}
				serverSocket.setSoTimeout(0);
			}
		} catch (IOException ie) {
		}
	}

	public static void main(String[] args) throws Exception {
		ChatServer chatServer = new ChatServer();
		chatServer.start();
	}
}