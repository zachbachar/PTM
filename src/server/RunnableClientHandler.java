package server;

import java.io.IOException;
import java.net.Socket;

import interfaces.ClientHandler;

public class RunnableClientHandler implements Runnable {

	private ClientHandler ch;
	private Socket socket;
	
	public  RunnableClientHandler(Socket _socket) throws IOException {
		this.socket = _socket;
		this.ch = new MyClientHandler(socket.getInputStream());
	}
	
	@Override
	public void run() {
		try {
			ch.handleClient(socket.getInputStream(), socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getPriority() {
		int x = ch.getPriority();
		System.out.println("priority: " + x);
		return x;	
	}
}
