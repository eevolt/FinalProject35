import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestClient {
	private static  PrintWriter out;
	private static BufferedReader in;
	private static Socket client;
	private static ServerSocket serverSocket;
	static pseudoServer server = new pseudoServer();
	ServerThread t = new ServerThread(server);
	
	public static void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);	
		} catch (java.lang.InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	public static void createClient() throws IOException {
		client = new Socket("localhost", 4444);
		System.out.println("Connected to " + client.getRemoteSocketAddress());
		out = new PrintWriter(client.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
	}
	
	//Before each test, initiliaze the CommandServer server
		@Before public void startServer() throws IOException {
			t.start();
			System.out.println("Started the pseudo Server");
		}
		//After each test, close the CommandServer server
		@After public void tearDownServer() throws IOException, SocketException{
			server.stopServer();
		}
	
	@Test
	public void testAddToOutputMsgQ() throws IOException {
		createClient();
		ChatClient cl = new ChatClient();
		String msg = "Hey there";
		cl.addToOutputMsgQ(msg);
		assertEquals("Strings should match", msg, cl.getOutMsgQ().get(0));
		client.close();
	}
	
	@Test
	public void testAddToInputMsgQ() throws IOException {
		createClient();
		ChatClient cl = new ChatClient();
		String msg = "Hey there";
		cl.addToInputMsgQ(msg);
		assertEquals("Strings should match", msg, cl.getInMsgQ().get(0));
		client.close();
	}
	
	@Test
	public void testPrintFromOutputMsgQ() throws IOException{
		createClient();
		ChatClient cl = new ChatClient();
		cl.testInitialize(client);
		String msg = "Hey there";
		cl.addToOutputMsgQ(msg);
		cl.printFromOutputMsgQ();
		client.close();
	}
	
	

}
