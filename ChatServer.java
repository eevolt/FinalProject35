
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    
    private static final int LISTENER_PORT = 5444;    
    private static ArrayList<String> usernames = new ArrayList<String>();    
    private static ArrayList<PrintWriter> serverOut = new ArrayList<PrintWriter>();


    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket serverSocket= new ServerSocket(LISTENER_PORT);
        serverSocket.setSoTimeout(60000);
        System.out.println("set timeout");
        try {        	
            while (true)  {  
            	if (usernames.size()>0){
                	serverSocket.setSoTimeout(0);
                }
                else{
                	serverSocket.setSoTimeout(60000);
                }
                new ServerHandler(serverSocket.accept(), serverOut, usernames).start(); 
                if (usernames.size()>0){
                	serverSocket.setSoTimeout(0);
                }
                else{
                	serverSocket.setSoTimeout(60000);
                }
            }
        } finally {
        	System.out.println("server socket closed");
            serverSocket.close();
        }
    }
}