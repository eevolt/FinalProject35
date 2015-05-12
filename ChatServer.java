
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    
    private static final int LISTENER_PORT = 5448;    
    private static ArrayList<String> usernames = new ArrayList<String>();    
    private static ArrayList<PrintWriter> serverOut = new ArrayList<PrintWriter>();


    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket serverSocket= new ServerSocket(LISTENER_PORT);
        String address= GeneralInetAddress.getLocalHost().getHostAddress();
        System.out.println("Address of ChatServer: "+address);
        serverSocket.setSoTimeout(60000);
        System.out.println("set timeout");
        try {        	
            while (true)  { 
            	
                new ServerHandler(serverSocket.accept(), serverOut, usernames).start();
                System.out.println("after"+ usernames.size());
                if (usernames.size()>0){
                	serverSocket.setSoTimeout(0);
                }
                else{
                	serverSocket.setSoTimeout(6000);
                }
            }
        } catch (IOException ie){
        	
        }finally {
        
        	System.out.println("server socket closed");
            serverSocket.close();
        }
    }
}