import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class pseudoServer extends Thread {
	private static final int LISTENER_PORT = 4444;
	private static ArrayList<String> usernames = new ArrayList<String>();
	private static ArrayList<PrintWriter> serverOut = new ArrayList<PrintWriter>();	
	private static ServerHandler handler;
	private ServerSocket serverSocket;
	private ChatServer cserver = new ChatServer();

	public void stopServer() {
		try {
			System.out.println("closed server\n");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ServerHandler getHandler(){
		return handler;
	}

	public void run() {
		try {
			System.out.println("Waiting for client to connect...");
			serverSocket = new ServerSocket(LISTENER_PORT);
			handler = new ServerHandler(serverSocket.accept(),
					serverOut, usernames, cserver);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
