
package chatserver;

import java.net.*;
import java.io.*;
import java.util.*;
import static chatserver.LogLevels.*;

/*
 * main class to run a server in a daemonize mode
 * server is binding to 41991 port by the default
 */
public class ServerCli {

	public final static int SERVER_PORT = 41991;
	ServerSocket serverSocket;
	int clientCounter;

	ServerCli(){
		clientCounter = 0;
	}

	public int getClientCounter(){
		return this.clientCounter;
	}

	public ServerSocket getServerSocket(){
		return this.serverSocket;
	}

	private void setServerSocket(ServerSocket ss){
		this.serverSocket = ss;
	}

	private void incrementClientCounter(){
		this.clientCounter += 1;
	}

	/*
	 * main
	 */
	public static void main (String args[]) throws IOException {

		ServerCli server = new ServerCli();
		server.setServerSocket(new ServerSocket(SERVER_PORT));
		chatserver.Logger.init();
		Logger.consoleLog(LOG_LEVEL_INFO, "Server started on " + SERVER_PORT + " port");

		//TODO instead of counter incrementation better to have some data structure stores unique client identifier i.e. <host>:<port>
		try {
			while (true) {
				Logger.consoleLog(LOG_LEVEL_INFO, "Server is ready to accept new connection from the client");
				Socket clientSocket = server.getServerSocket().accept();
				Logger.consoleLog(LOG_LEVEL_INFO, "\tNew client is trying to connect to server");
				Logger.consoleLog(LOG_LEVEL_INFO, "\tConnected client socket port : " + clientSocket.getPort());
				Logger.consoleLog(LOG_LEVEL_INFO, "\tConnected client socket host : " + clientSocket.getInetAddress());
				server.incrementClientCounter();
				MyConnection con = new MyConnection(server.getClientCounter(), clientSocket);
				con.start();
				Logger.consoleLog(LOG_LEVEL_INFO, "\tNew client connection successfully established");
			}
		//TODO main method throws and catches IOException ??
		} catch (IOException e) {
			Logger.consoleLog(LOG_LEVEL_ERROR, "Has been catched: serverCli: " + e.toString());
		}
	}
}


