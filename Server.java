import java.net.*;
import java.io.*;
import java.util.*;
public class Server extends Thread{
	public static final int LISTENER_PORT=5065;
	private ArrayList<String> messageQueue = new ArrayList<String>();
	private ArrayList<ChatClient> clientList= new ArrayList<ChatClient>();
	
	public synchronized void addClient(ChatClient client){
		clientList.add(client);
	}
	public synchronized void removeClient(ChatClient client){
		if (clientList.indexOf(client)!=-1){
			clientList.remove(clientList.indexOf(client));
		}
	}
	
	public synchronized void addToMsgQueue(ChatClient client, String new_message){
		Socket socket=client.getSocket();
		String sender= client.getName();
		new_message= sender+": "+new_message;
		messageQueue.add(new_message);
		notify();
	}
	
	public synchronized void sendMessage() throws InterruptedException{
		while (messageQueue.size()==0){
			wait();
		}
		String message= messageQueue.remove(0);
		for (int i=0; i<clientList.size(); i++){
			clientList.get(i).sendMessage(message);
		}
	}
	public void run(){
		try{
		while(true){
			sendMessage();}	
		}catch(InterruptedException ie){
			ie.printStackTrace();
		}
	}
	public static void main(String[] args) {
		ServerSocket serverSocket=null;
		try{
			serverSocket= new ServerSocket(LISTENER_PORT);
			System.out.println("Server started on port "+LISTENER_PORT);
		}catch (IOException ie){
			System.err.println("Cannot listen on port "+LISTENER_PORT);
			System.exit(-1);
		}
		Server server= new Server();
		server.start();
		while (true){
			try{
				Socket socket=serverSocket.accept();
				ChatClient client= new ChatClient(server, socket);	
				server.start();
			}catch (IOException ie){
				ie.printStackTrace();
			}
		}

	}

}
