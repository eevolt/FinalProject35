
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    
    private static final int LISTENER_PORT = 9016;    
    private static ArrayList<String> usernames = new ArrayList<String>();    
    private static ArrayList<PrintWriter> serverOut = new ArrayList<PrintWriter>();


    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket serverSocket= new ServerSocket(LISTENER_PORT);
        try {
            while (true) {
                new ServerHandler(serverSocket.accept(), serverOut, usernames).start();
            }
        } finally {
            serverSocket.close();
        }
    }
}