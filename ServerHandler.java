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
	private ChatServer chatServer;

	public ServerHandler(Socket socket, ArrayList<PrintWriter> writers,
			ArrayList<String> names, ChatServer chatServer) {
		this.socket = socket;
		this.serverOut = writers;
		this.usernames = names;
		this.chatServer=chatServer;
	}

	public Socket getSocket(){
		return socket;
	}
	public ArrayList<PrintWriter> getIOStreams(){
		return serverOut;
	}
	public ArrayList<String> getUsernames(){
		return usernames;
	}
	public ChatServer getServerInstance(){
		return chatServer;
	}
	public ArrayList<String> getMsgQ(){
		return messageQueue;
	}
	
	public void toTest() throws IOException{
		sIn = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		sOut = new PrintWriter(socket.getOutputStream(), true);
		serverOut.add(sOut);
	}
	
	public synchronized void addToMsgQueue(String new_message) {
		messageQueue.add(new_message);
		//System.out.println("message added to server message queue");
		notify();
	}

	public synchronized void sendMessage() throws InterruptedException {
		while (messageQueue.size() == 0) {
			//System.out.println("waiting on sending message...");
			wait();
		}
		while (messageQueue.size() > 0) {
			String message = messageQueue.remove(0);
			//System.out.println("Sever msgQ: " + message);
			for (PrintWriter toClient : serverOut) {
				toClient.println(message);
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

			sOut.println("SOUNDSGOOD ");
			synchronized(serverOut){
				serverOut.add(sOut);
			}
			
			addToMsgQueue("NEWUSER "+username+" has entered the Nightosphere");
			try{
				sendMessage();				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (true) {
				String msg = sIn.readLine();
				if (msg == null) {
					return;
				}
				addToMsgQueue("MESSAGE "+username+": "+msg);
				try {
					sendMessage();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			synchronized(usernames){
				if (username != null) {
					usernames.remove(username);}
				if (usernames.size()==0){					
					chatServer.stopServer();
			}}
			synchronized(serverOut){
				if (sOut != null) {
					serverOut.remove(sOut);
				}
			}
			
			try {
				addToMsgQueue("MESSAGE "+username+" has left the Nightosphere");
				try {
					sendMessage();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					this.join();
					System.out.println("joined ServerHandler");
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
