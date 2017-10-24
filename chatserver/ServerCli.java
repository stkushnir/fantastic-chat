
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

	public static int SERVER_PORT = 41991;
	public static ServerSocket serverSocket;
	public static int clientCounter = 0;

	/*
	 * main
	 */
	public static void main (String args[]) throws IOException {
		serverSocket = new ServerSocket(SERVER_PORT);
		chatserver.Logger.init();
		Logger.consoleLog(LOG_LEVEL_INFO, "Server started on " + SERVER_PORT + " port");

		//TODO instead of counter incrementation better to have some data structure stores unique client identifier i.e. <host>:<port>
		try {
			while (true) {
				Logger.consoleLog(LOG_LEVEL_INFO, "Server is ready to accept new connection from the client");
				Socket clientSocket = serverSocket.accept();
				Logger.consoleLog(LOG_LEVEL_INFO, "\tNew client is trying to connect to server");
				Logger.consoleLog(LOG_LEVEL_INFO, "\tConnected client socket port : " + clientSocket.getPort());
				Logger.consoleLog(LOG_LEVEL_INFO, "\tConnected client socket host : " + clientSocket.getInetAddress());
				clientCounter++;
				MyConnection con = new MyConnection(clientCounter, clientSocket);
				con.start();
				Logger.consoleLog(LOG_LEVEL_INFO, "\tNew client connection successfully established");
			}
		//TODO main method throws and catches IOException ??
		} catch (IOException ex) {
			Logger.consoleLog(LOG_LEVEL_ERROR, "Has been catched: serverCli: " + ex.toString());
		}
	}
}


