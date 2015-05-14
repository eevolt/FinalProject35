import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer extends Thread {

	private static final int LISTENER_PORT = 5449;
	private static ArrayList<String> usernames = new ArrayList<String>();
	private static ArrayList<PrintWriter> serverOut = new ArrayList<PrintWriter>();
	public static boolean hasClients = true;

	public void run() {
		ArrayList<ServerHandler> handler_list = new ArrayList<ServerHandler>();
		System.out.println("The chat server is running.");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(LISTENER_PORT);
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
		// serverSocket.setSoTimeout(60000);
		System.out.println(hasClients);
		try {
			
				while (hasClients) {
					System.out.println("accepting clients...");
					ServerHandler sh = new ServerHandler(serverSocket.accept(),
							serverOut, usernames);
					if (sh != null) {
						sh.start();
					}
					// serverSocket.setSoTimeout(0);
				}
			
			System.out.println("server socket closed");
			serverSocket.close();
		} catch (IOException ie) {
		}
	}

	public static void main(String[] args) throws Exception {
		ChatServer chatServer = new ChatServer();
		chatServer.start();
	}
}