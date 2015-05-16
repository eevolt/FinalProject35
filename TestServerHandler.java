import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestServerHandler {
	//in order to test server client functions
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
		//ClientThread c = new ClientThread(in);
		//c.start();
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
	public void testConstructor() throws IOException {
		createClient();
		ArrayList<String> names = new ArrayList<String>() {
			{
				add("l33t_h@cker420");
			}};
			ArrayList<PrintWriter> serverOut = server.getHandler().getIOStreams();
			ChatServer s = new ChatServer();
			Socket soc = server.getHandler().getSocket();
			ServerHandler sh = new ServerHandler(soc, serverOut, names, s);

			assertEquals("Was not the same socket", soc, sh.getSocket());
			assertEquals("Was not the same array of writer", serverOut, sh.getIOStreams());
			assertEquals("Was not the same list of usernames", names, sh.getUsernames());
			assertEquals("Was not the same server instance", s, sh.getServerInstance());

			client.close();
	}

	@Test
	public void testAddToMsgQueue() throws IOException{

		createClient();
		ArrayList<String> names = new ArrayList<String>() {
			{
				add("l33t_h@cker420");
			}};
			Socket socket = server.getHandler().getSocket();
			ArrayList<PrintWriter> serverOut = server.getHandler().getIOStreams();
			ChatServer s = new ChatServer();

			ServerHandler sh = new ServerHandler(socket, serverOut, names, s);
			String message1 = "(UN)ACCEPTABLE!";
			String message2 = "Who wants to play video games?";

			sh.addToMsgQueue(message1);
			sh.addToMsgQueue(message2);

			//Check to make sure that messags were placed in order on the queue
			//Message2 should be last on the queue
			System.out.println("Message1: " + sh.getMsgQ().get(0) + "\nMessage2: " + sh.getMsgQ().get(1));
			assertEquals("Message 1 not the same", message1, sh.getMsgQ().get(0));
			assertEquals("Message 2 not the same", message2, sh.getMsgQ().get(1));

			client.close();
	}

	@Test
	public void testSendMessage() throws IOException, InterruptedException{
		createClient();

		ArrayList<String> names = new ArrayList<String>() {
			{
				add("l33t_h@cker420");
			}};
			Socket socket = server.getHandler().getSocket();
			ArrayList<PrintWriter> serverOut = server.getHandler().getIOStreams();
			ChatServer s = new ChatServer();

			ServerHandler sh = new ServerHandler(socket, serverOut, names, s);
			sh.toTest();
			String message1 = "(UN)ACCEPTABLE!";

			sh.addToMsgQueue(message1);
			sh.sendMessage();
			System.out.println(sh.getMsgQ().isEmpty());

			String msg = in.readLine();
			System.out.println(msg);
			client.close();
	}

	@Test
	public void testSetUsername() throws IOException {
		createClient();
		ArrayList<String> msgQ = new ArrayList<String>() {
			{   add("(UN)ACCEPTABLE!");
			}};
			ArrayList<String> names = new ArrayList<String>() {
				{
					add("l33t_h@cker420");
				}};
				Socket socket = server.getHandler().getSocket();
				ArrayList<PrintWriter> serverOut = server.getHandler().getIOStreams();
				ChatServer s = new ChatServer();

				ServerHandler sh = new ServerHandler(socket, serverOut, names, s);
				sh.toTest();
				out.println("Florrey");
				sleep(500);
				sh.setUsername();
				System.out.println(sh.getUsernames().get(1)); //should say Florrey		
		client.close();
	}

}
