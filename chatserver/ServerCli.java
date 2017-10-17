
package chatserver;

import java.net.*;
import java.io.*;
import java.util.*;

/*
 * main class to run a server in a daemonize mode
 * server is binding to 41991 port by the default
 */
public class ServerCli {

	public static int SERVER_PORT = 41991;
	public static ServerSocket serverSocket;
	public static int clientCounter = 0;

	/*
	 * main
	 */
	public static void main (String args[]) throws IOException {
		serverSocket = new ServerSocket(SERVER_PORT);
		chatserver.Logger.init();
		Logger.consoleLog("info", "Server started on " + SERVER_PORT + " port");

		//TODO instead of counter incrementation better to have some data structure stores unique client identifier i.e. <host>:<port>
		try {
			while (true) {
				Logger.consoleLog("info", "Server is ready to accept new connection from the client");
				Socket clientSocket = serverSocket.accept();
				Logger.consoleLog("info", "\tNew client is trying to connect to server");
				Logger.consoleLog("info", "\tConnected client socket port : " + clientSocket.getPort());
				Logger.consoleLog("info", "\tConnected client socket host : " + clientSocket.getInetAddress());
				clientCounter++;
				MyConnection con = new MyConnection(clientCounter, clientSocket);
				con.start();
				Logger.consoleLog("info", "\tNew client connection successfully established");
			}
		//TODO main method throws and catches IOException ??
		} catch (IOException ex) {
			Logger.consoleLog("error", "Has been catched: serverCli: " + ex.toString());
		}
	}
}


