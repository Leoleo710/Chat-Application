import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
 
public class ChatClient extends Frame {

	/**
	 * @param args
	 */

	TextField tfTxt = new TextField();
	TextArea taContent = new TextArea();
	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	private boolean bConnected = false;	
	Thread tRecv = new Thread(new RecvThread());
	
	public static void main(String[] args) {
		new ChatClient().launchFrame();
	}

	public void launchFrame() {
		// Set location and size for the frame
		setLocation(400, 300);
		this.setSize(300, 300);
		// Add text field and text area for the frame
		add(tfTxt, BorderLayout.SOUTH);
		add(taContent, BorderLayout.NORTH);
		// Pack the text field, text area and frame together.
		pack();
		// Add the window event--closing the window
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				disconnect();
				System.exit(0);
			}
		});
		// Set the listener for the text field
		tfTxt.addActionListener(new TextFieldListener());
		// Set the window visible.
		setVisible(true);
		// Connect with server as soon as the frame is launched
		connect();	
		tRecv.start();
	}

	/*
	 * Connect
	 */
	public void connect() {
		// Send a client connect request to (server IP).
		// Benny IP: 172.20.190.38
		try {
			s = new Socket("172.20.190.38", 8888);
			
			// Create a data output stream
			// s.getOutputStream() is the output stream that Socket s got
			dos = new DataOutputStream(s.getOutputStream());
			// Create a data input stream in order to send the received messages to other clients 
			dis = new DataInputStream(s.getInputStream());
			System.out.println("Connected");	
			bConnected = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			dos.close();
			dis.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	
	/*
	 * After typing in the text field, the words typed will display in the text
	 * area and delete the white space.
	 */
	private class TextFieldListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// Get the string from text field
			String str = tfTxt.getText().trim();
			// After entering "return", the text field will type empty string
			// so that the text field will display nothing.
			tfTxt.setText("");

			try {
				// Write the string that client typed into output stream
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private class RecvThread implements Runnable{
		
		@Override
		public void run() {
			try{
				while(bConnected){
					String str = dis.readUTF();
					//System.out.println(str);
					taContent.setText(taContent.getText() + str + '\n');
				}
			}
			catch(SocketException e){
				System.out.println("Logging out.");
			}catch(EOFException e){
				System.out.println("Logging out.");
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
		}
		
	}
}
