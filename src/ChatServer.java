import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
  
public class ChatServer {

	/**
	 * @param args
	 */
	// Determine if the server is started
	private boolean started = false;
	private ServerSocket ss = null;
	// Contain the clients in the list in order to save and reuse
	List<Client> clients = new ArrayList<Client>();
	
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	/*
	 * This method is to create an instance of Client in main(static) 
	 * and start the thread of that instance. 
	 */
	public void start() {
		try {
			// Create a server that is bind port 8888
			ss = new ServerSocket(8888);
			// The server is started
			started = true;
		} catch (BindException b) {
			System.out.println("The port is already existed.");
			System.out.println("Please close the server and try again.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			while (started) {
				// Determine if the connected with the client
				// boolean bConnected = false;
				// Get the connection request from client and set it to 's'
				Socket s = ss.accept();
				Client c = new Client(s);
				System.out.println("A Client is Connected!");// When client send
																// connect
																// request, this
																// will be
																// displayed

				new Thread(c).start();
				// Add the new client into the list
				clients.add(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * This inner class is to implement Runnable interface.
	 * When the instance of this class is created, the client 
	 * and the server will be connected.
	 */
	class Client implements Runnable {

		private Socket s;
		private DataInputStream dis = null;
		private boolean bConnected = false;
		private DataOutputStream dos = null;
		
		public Client(Socket s) {
			this.s = s;
			try {
				// Create a data input stream
				// s.getInputStream() is the input stream that Socket s got
				dis = new DataInputStream(s.getInputStream());
				// Create a data output stream in order to send it to other clients
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("Logging out.");
				//e.printStackTrace();
			}
		}
		
		@Override
		/*
		 * This method is defined to read and print the input from the client.
		 */
		public void run() {
		
			try {
				while (bConnected) {
					String str = dis.readUTF();
					System.out.println(str);
					for(int i = 0; i < clients.size(); i++){
						Client c = clients.get(i);
						// Send the message to all clients
						c.send(str);
					}
					
				}
			} 

			catch (EOFException e) {
				System.out.println("The Client Is Closed.");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					// When the client is disconnected, both data input stream
					// and socket will be closed
					if (dis != null)
						dis.close();
					if (dos != null)
						dos.close();
					if (s != null){
						s.close();
						s = null;
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
