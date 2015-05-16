import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class ServerThread extends Thread{
	private pseudoServer server;

	public ServerThread(pseudoServer serv){
		server = serv;
	}

	public void run(){
		System.out.println("About to start the pseudoServer from the Thread");
		server.run();
	}
	
}