import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ChatClient {
	ArrayList<String> titles = new ArrayList<String>() {
		{
			add("Cosmic Owl");
			add("James Baxter");
			add("Peppermint Butler");
			add("Jake the Dog");
			add("Finn the Human");
			add("Lady Rainicorn");
			add("Gunther");
			add("Ice King");
			add("Marceline");
			add("Lemonhope");
			add("Lemongrab");
			add("Prismo");
			add("Flame Princess");
			add("BMO");
			add("Susan Strong");
			add("The Lich");
			add("Lumpy Space Princess");
			add("Lord Monochromicorn");
			add("Cake the Cat");
			add("Fiona the Human");
			add("Prince Gumball");
			add("Marshall Lee");
		}
	};
	private static int SERVER_PORT=5451;
	JFrame frame = null;
	BufferedReader clientIn;
	PrintWriter clientOut;

	JTextField textField = new JTextField(60);
	JTextPane messageArea = new JTextPane();

	StyledDocument doc = messageArea.getStyledDocument();
	SimpleAttributeSet messageText = new SimpleAttributeSet();
	JScrollPane scrollPane= new JScrollPane(messageArea);
	ArrayList<String> outputMsgQ = new ArrayList<String>();
	ArrayList<String> inputMsgQ = new ArrayList<String>();

	public ChatClient() {
		Random rand = new Random();
		int ind = rand.nextInt(titles.size() - 1);
		frame = new JFrame(titles.get(ind));
		textField.setEditable(false);
		messageArea.setEditable(false);
		messageArea.setPreferredSize(new Dimension(290,300));
		messageArea.setForeground(Color.GREEN);
		messageArea.setText("Welcome To The Nightosphere \n");
		StyleConstants.setForeground(messageText, Color.green);
		StyleConstants.setBackground(messageText, Color.black);
		StyleConstants.setBold(messageText, true);
		((JComponent) frame.getContentPane()).setBorder(BorderFactory
				.createMatteBorder(5, 5, 5, 5, Color.green));
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel chatPanel = new JPanel(new GridBagLayout());
		messageArea.setOpaque(true);
		messageArea.setBackground(Color.BLACK);
		
		//add the scrollpane
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        chatPanel.add(scrollPane, constraints);        
        
		frame.getContentPane().add(textField, "Center");		
		frame.getContentPane().add(chatPanel, "North");
		frame.pack();

		textField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// out.println(textField.getText());

				addToOutputMsgQ(textField.getText());
				printFromOutputMsgQ();
				textField.setText("");
			}
		});
	}
	
	public void testInitialize(Socket socket) throws IOException{
		clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		clientOut = new PrintWriter(System.out, true);
	}
	public BufferedReader getIOIn(){
		return clientIn;
	}
	public PrintWriter getIOout(){
		return clientOut;
	}
	public ArrayList<String> getOutMsgQ(){
		return outputMsgQ;
	}
	public ArrayList<String> getInMsgQ(){
		return inputMsgQ;
	}
	
	
	public synchronized void addToOutputMsgQ(String s) {
		outputMsgQ.add(s);
		notify();
	}

	public synchronized void printFromOutputMsgQ() {

		try {
			while (outputMsgQ.size() == 0) {
				wait();
			}
			while (outputMsgQ.size() > 0) {
				clientOut.println(outputMsgQ.remove(0));
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
			}
		} catch (InterruptedException e) {
		}
	}
	public synchronized void addToInputMsgQ(String s){
		inputMsgQ.add(s);
		notify();
	}
	public synchronized void displayFromInputMsgQ(){
		try{
			while(inputMsgQ.size()==0){
				wait();
			}
			while(inputMsgQ.size()>0){
				String input=inputMsgQ.remove(0);
				if (input.startsWith("USERNAME")) {
					String user = getUserName();
					clientOut.println(user);
				} else if (input.startsWith("SOUNDSGOOD")) {
					textField.setEditable(true);
				} else if (input.startsWith("MESSAGE")) {
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					append(dateFormat.format(date) + "  " + input.substring(8)
							+ "\n");
				} else if (input.startsWith("NEWUSER")) {
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					append(dateFormat.format(date) + "  " + input.substring(8)
							+ "\n");
				}
			}
		}catch (InterruptedException e) {
		}
	}

	private String getServerIP() {
		return JOptionPane.showInputDialog(frame, "IP of Server:",
				"Welcome to the Nightosphere", JOptionPane.QUESTION_MESSAGE);
	}

	private String getUserName() {
		return JOptionPane.showInputDialog(frame, "Pick your screen name:",
				"Screen name", JOptionPane.PLAIN_MESSAGE);
	}

	private void append(String s) {
		try {
			doc.insertString(doc.getLength(), s, messageText);
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void run() throws IOException {

		String serverAddress = getServerIP();
		Socket socket = new Socket(serverAddress,SERVER_PORT);
		clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		clientOut = new PrintWriter(socket.getOutputStream(), true);
		String user = "";

		while (true) {
			String input = clientIn.readLine();
			if (input != null) {
				addToInputMsgQ(input);
				displayFromInputMsgQ();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		ChatClient client = new ChatClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}
}