
package chatserver;

import java.net.*;
import java.io.*;
import java.util.*;
import static chatserver.LogLevels.*;

/*
 * class [extends Thread] to cooperate with all connected clients
 * @conctructor MyConnection
 * @method sendMessageConnectedClients
 * @method run
 */
class MyConnection extends Thread {

	private BufferedReader in;
	private OutputStream out;
	private Socket clientSocket;
	private static HashMap<Integer,Socket> allConnected = new HashMap<Integer,Socket>();
	private int currentKey; //FIXME rename

	/*
	 * @constructor
	 * puts connected client into HashMap <Integer,Socket>
	 * @param int clientCounter
	 * @param Socket clientSocket
	 */
	public MyConnection(int clientCounter, Socket clientSocket) {
		this.clientSocket = clientSocket;
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			allConnected.put(clientCounter, clientSocket);
		} catch (IOException ioExc) {
			Logger.consoleLog(LOG_LEVEL_ERROR, "MyConnection constructor: " + ioExc.toString() + ". Exiting");
			System.exit(1);
		}
	}

	/*
	 * sends String message to all connected clients set
	 * @param String -- message which needs to be sent
	 * FIXME broken pipe exception treatment
	 * FIXME notification to clients that someone logged off
	 * FIXME new just logged in client sees messages that someone already left
	 */
	public void sendMessageConnectedClients (String sendTextMessage) throws IOException {
		try {
			Set<Map.Entry<Integer,Socket>> set = allConnected.entrySet();
			for (Map.Entry<Integer,Socket> recipient : set) {
				currentKey = recipient.getKey();
				out = recipient.getValue().getOutputStream();
				out.write(sendTextMessage.getBytes());
			}
		} catch (java.net.SocketException ex) {
			/*
			 * broken pipe exception is being catched when server tries to
			 * write to a connection when the other end has already closed it
			 * appeares when i-client from [0;M] connected clients logged off
			 * FIXME each client from [i+1;M] set would not recieve a message
			 */
			allConnected.remove(currentKey);
			Logger.consoleLog(LOG_LEVEL_WARN, "One of the clients already disconnected: " + ex);
			// FIXME LEFT2 shit-message appeares in GUI, might be not-server bug
			sendMessageConnectedClients("\0LEFT\0" + currentKey);
		}
	}

	/*
	 * new client thread creation with run() method
	 * reads int->char symbols and does:
	 * - in case of EOM delimiter - sending to all connected clients
	 * - in other case - addition of char symbol to existing message
	 */
	public void run() {
		try {
			String sendTextMessage = "";
			int intReceived;
			while ((intReceived = in.read()) != -1) {
				char charReceived = (char) intReceived;
				switch (charReceived) {
					case '\n': //TODO decide what delimiter to use??
						Logger.consoleLog(LOG_LEVEL_INFO, "Received message --> " + sendTextMessage);
						sendMessageConnectedClients(sendTextMessage);
						sendTextMessage = "";
						break;
					default:
						sendTextMessage += charReceived;
				}
			}
		} catch (IOException ioExc) {
			Logger.consoleLog(LOG_LEVEL_ERROR, "MyConnection.run() method: " + ioExc.toString() + ". Exiting");
			System.exit(1);
		}
	}
}


