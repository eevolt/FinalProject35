import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class ClientListener {
	private BufferedReader pOut;
	private Socket socket;
	private Server server;
	private ChatClient client;
	private String name;
	
	public ClientListener(ChatClient client) {
		this.client=client;
		this.server = client.getServer();		
		this.socket = socket;
		try {
			pOut = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	public void run(){
		try{
			while(pOut.readLine()==null){
				wait();
			}
			server.addToMsgQueue(client, pOut.readLine());
		}catch(IOException ioe){
			ioe.printStackTrace();
		}catch(InterruptedException ie){
			ie.printStackTrace();
		}
		server.removeClient(client);
		
	}
}
