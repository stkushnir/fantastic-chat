
package chatclient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;

/*
 * used to initiate GUI-based client with send message functionality
 * @constructor ChatCli
 * @method actionPerformed
 * @method sendMessageToServer
 */
public class ChatCli extends JFrame implements ActionListener {

	private static final int SERVER_PORT = 41991;
	private static final String DEFAULT_ENCODING = "UTF-8";

	public static String nickName;
	static DefaultStyledDocument doc;
	public static InputStream myIn;
	public static OutputStream myOn;
	static JTextPane messageHistory;
	JTextField messageBody; 

	/*
	 * @constructor to makeGUI
	 * TODO need to get rid of hardcoded GUI sizes
	 */
	public ChatCli() {

		// code below makes JFrame
		// code below makes stable JTextPane (where all messages are being shown)
		// code below makes editable JTextField (where message is being typed)

		setSize(900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		messageHistory = new JTextPane(doc);
		messageHistory.setEditable(false);
		messageBody = new JTextField();
		add(messageHistory, BorderLayout.CENTER);
		add(messageBody, BorderLayout.SOUTH);
		messageBody.addActionListener(this);

		//code below makes vertical JScrollPane to be auto scrolled down
		DefaultCaret caret = (DefaultCaret) messageHistory.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane jspVertical = new JScrollPane(messageHistory);
		add(jspVertical);
		jspVertical.setViewportView(messageHistory);
	}

	/*
	 * triggered by listener of JTextField
	 * executed when <ENTER> is pressed (default)
	 */
	public void actionPerformed(ActionEvent ae) {
		sendMessageToServer(messageBody.getText());
		messageBody.setText("");
	}

	/*
	 * sends client message to server
	 * TODO should return TRUE/FALSE flag
	 */
	public static void sendMessageToServer (String typedTextMessage) {

		// if no message (only <ENTER> pressed) - no need to send it to server
		if (typedTextMessage.equals("")) {
			return;
		}

		// here message is being sent to OUT stream and passed to server
		try {
			myOn.write((nickName + ":" + typedTextMessage + "\n").toString().getBytes(DEFAULT_ENCODING));
		} catch (Exception e) {
			//FIXME client should not be terminated but delivery failed should appear
			System.exit(1);
		}
	}

	/*
	 * main
	 * FIXME exception in thread "main" java.net.ConnectException: Connection refused
	 * happens when client starts and no running Server available to connect to
	 */
	public static void main (String args[]) throws UnknownHostException, IOException {
		/*
		 * @todo of course no position parameters options are possible :-)
		 */
		nickName = (args.length > 0) ? args[0] : "DEFAULT";
		String serverName = (args.length > 1) ? args[1] : "localhost";

		InetAddress hostName = InetAddress.getByName(serverName); //FIXME no need to do this
		Socket socket = new Socket(hostName, SERVER_PORT);
		myIn = socket.getInputStream();
		myOn = socket.getOutputStream();
		doc = new DefaultStyledDocument();
		ChatCli instance = new ChatCli();
		instance.setVisible(true);
		new ListenerThread(messageHistory, doc, myIn, myOn);
	}
}

