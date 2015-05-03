import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ChatClient extends Frame implements Runnable {
	private ArrayList<String> messageQueue = new ArrayList<String>();
	private PrintWriter pOut;
	private BufferedReader pIn;
	private Socket socket;
	private Server server;
	private String username;

	// Change to AA1635-2.mit.edu ip address
	public static final String SERVER_HOST = "localhost";
	public static final int SERVER_PORT = 5065;

	public ChatClient(Server server, Socket socket) throws IOException{
		this.server = server;
		this.socket = socket;
		try {
			pOut = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			pIn = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public synchronized void addMessage(String message) {
		messageQueue.add(message);
		notify();
	}

	public Server getServer(){
		return server;
	}
	public synchronized String getMessage() throws InterruptedException {
		while (messageQueue.size() == 0) {
			wait();
		}
		String message = messageQueue.remove(0);
		return message;
	}

	public void msgToClient(String message) {
		pOut.println(message);
		pOut.flush();
	}

	public Socket getSocket() {
		return socket;
	}

	public void run() {
		try {
			while(true){
			String message = getMessage();
			msgToClient(message);}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	public void sendToServer(String message) {
		server.addToMsgQueue(this, message);

	}

	public static void main(String[] args) {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			Socket serverSocket = new Socket(SERVER_HOST, SERVER_PORT);
			in = new BufferedReader(new InputStreamReader(
					serverSocket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(
					serverSocket.getOutputStream()));
			System.out.println("Connected to server " + SERVER_HOST + ":"
					+ SERVER_PORT);
		} catch (IOException ioe) {
			System.err.println("Couldn't connect to " + SERVER_HOST + ":"
					+ SERVER_PORT);
			System.exit(-1);
		}
		ClientWindow cw = new ClientWindow(in, out);

	}

}
