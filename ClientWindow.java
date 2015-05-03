import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ClientWindow extends JFrame implements Runnable{
	private PrintWriter pOut;
	private BufferedReader pIn;
	private TextField text_input;
	private TextArea msg_history;
	private String username;
	public ClientWindow(BufferedReader in, PrintWriter out) {
		pIn=in;
		pOut=out;
		setUsername();
		JFrame frame = new JFrame("Cosmic Owl");
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		text_input = new TextField();
		msg_history = new TextArea();
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1,
				Color.green));
		contentPane.add("North", msg_history);
		contentPane.add("Center", text_input);
		text_input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processMessage(e.getActionCommand());
			}
		});
		this.run();
	}

	public void setUsername() {
		JFrame frame = new JFrame("Set Username");
		String s = (String) JOptionPane.showInputDialog("Set username: ");
		username = s;
	}
	public void processMessage(String msg){
		pOut.println(username+": "+msg);
		notify();
		text_input.setText("");
	}
	public void run() {
		try{
			while(true){
				msg_history.append(pIn.readLine());
			}
		}catch(IOException ie){
			ie.printStackTrace();
		}
	}
}
