package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import interfaces.ClientHandler;

import interfaces.Server;
import utils.PriorityJobScheduler;

public class MyServer implements Server {
	
	private ServerSocket serverSocket;
    private int port;
    private boolean stop = false;
    private PriorityJobScheduler jobScheduler;
    
	public MyServer(int port){
		this.port = port;
	}
	
	private void startServer(ClientHandler clientHandler) throws IOException {
        //System.out.println("MyServer.startServer();");
		serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000);
        System.out.println("Server connected - waiting");
        jobScheduler = new PriorityJobScheduler(2, 2);
 
        while (!stop) {
            try {
                Socket aClient = serverSocket.accept();
                System.out.println("client connected");
                RunnableClientHandler rch = new RunnableClientHandler(aClient);
                jobScheduler.scheduleJob(rch);
                
            } catch (SocketTimeoutException e) {
            		//System.out.println("Client did not connect...");
            }
        }
        //System.out.println("server stopped");
        serverSocket.close();
    }

	@Override
	public void start(ClientHandler clientHandler) throws IOException {
		//System.out.println("MyServer.start();");
		new Thread(() -> {
	            try {
	                startServer(clientHandler);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }).start();
	}

	@Override
	public synchronized void stop() {
		this.stop = true;
		System.out.println("Done");
	}

}