import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient {

	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame("Cosmic Owl");
	JTextField textField = new JTextField(60);
	JTextArea messageArea = new JTextArea(10, 60);
	private ArrayList<String> msgQ = new ArrayList<String>();

	public ChatClient() {
		
		textField.setEditable(false);
		messageArea.setEditable(false);
		((JComponent) frame.getContentPane()).setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1,
				Color.green));
		frame.getContentPane().add(textField, "Center");
		frame.getContentPane().add(new JScrollPane(messageArea), "North");
		frame.pack();

		
		textField.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//out.println(textField.getText());
				
				addToMsgQ(textField.getText());
				printFromMsgQ();
				textField.setText("");
			}
		});
	}

	public synchronized void addToMsgQ(String s) {
		msgQ.add(s);
		notify();
	}

	public synchronized void printFromMsgQ() {
		
		try {
			while (msgQ.size() == 0) {
				wait();
			}
			while (msgQ.size()>0){
			System.out.println("msgQ: " + msgQ.get(0));
			out.println(msgQ.remove(0));}
		} catch (InterruptedException e) {
		}
	}

	private String getServerAddress() {
		return JOptionPane.showInputDialog(frame,
				"IP of Server:", "Welcome to the Nightosphere",
				JOptionPane.QUESTION_MESSAGE);
	}

	
	private String getName() {
		return JOptionPane.showInputDialog(frame, "Pick your screen name:",
				"Screen name", JOptionPane.PLAIN_MESSAGE);
	}

	
	private void run() throws IOException {

		
		String serverAddress = getServerAddress();
		Socket socket = new Socket(serverAddress, 9016);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		
		while (true) {
			String line = in.readLine();
			if (line!=null){
			if (line.startsWith("USERNAME")) {
				out.println(getName());
			} else if (line.startsWith("SOUNDSGOOD")) {
				textField.setEditable(true);
			} else if (line.startsWith("MESSAGE")){
				messageArea.append(line.substring(8) + "\n");
			}}
		}
	}

	public static void main(String[] args) throws Exception {
		ChatClient client = new ChatClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}
}