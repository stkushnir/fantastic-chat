
package chatclient;

import java.awt.Color;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/*
 * class [extends ChatCli] is meant to listen to server while client works and show the new info in GUI
 * @constructor ListenerThread
 * @method getRandomUserColor
 * @method run
 */
public class ListenerThread extends ChatCli implements Runnable {

	private final static int MAX_PACKAGE_SIZE = 1024;
	Thread lt;
	StyleContext cont = StyleContext.getDefaultStyleContext();
	//FIXME random color is entirely applied for all !ME user for Document
	AttributeSet attrRnd = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getRandomUserColor());
	AttributeSet attrMine = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
	AttributeSet attrGray = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.GRAY);
	AttributeSet attrDef = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);

	/*
	 * @constructor
	 * @param JTextPane -- pane where all GUI information is being posted
	 * @param DefaultStyledDocument
	 * @param InputStream of a client socket
	 * @param OutputStream of a client socket
	 */
	ListenerThread (JTextPane messageHistHistory, DefaultStyledDocument doc, InputStream myIn, OutputStream myOn) {
		this.myIn = myIn;
		this.myOn = myOn;
		this.doc = doc;
		this.messageHistory = messageHistory;
		lt = new Thread(this);
		lt.start();
	}

	/*
	 * takes random integer values for each RGB component
	 * mixes new Color based on RGB combination above
	 * @return Color
	 */
	private static Color getRandomUserColor() {
		int randomRed = (int) (255 * Math.random());
		int randomGre = (int) (255 * Math.random());
		int randomBlu = (int) (255 * Math.random());
		Color randomColor = new Color(randomRed, randomGre, randomBlu);
		return randomColor;
	}

	//FIXME messy code
	public void run() {
		while (true) {
			try {
				byte showBuffer[] = new byte[MAX_PACKAGE_SIZE];
				int showInput = myIn.read(showBuffer);
				//fullMessage - plain text string came from server
				String fullMessage = new String(showBuffer, 0, showInput, "UTF-8");
				String allInfoFromServer[] = fullMessage.split(":",2);
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("KK:mm:ss aa");
				//FIXME LEFT2 shit-message appeares in GUI, might be not-client bug
				if (fullMessage.startsWith("\0LEFT\0")) {
					doc.insertString(doc.getLength(), "[" + sdf.format(date) + "] " +
					fullMessage.replace("\0LEFT\0","") + " has already left the chat. He will be back. Promise.\n", attrGray);
				} else {
					doc.insertString(doc.getLength(), ("[" + sdf.format(date) + "] "), attrGray);
					// if message belonges to sender - standard color.BLUE is used
					if (allInfoFromServer[0].equals(nickName)) {
						doc.insertString(doc.getLength(), allInfoFromServer[0], attrMine);
					} else {
						doc.insertString(doc.getLength(), allInfoFromServer[0], attrRnd);
					}
					doc.insertString(doc.getLength(), (" : " + allInfoFromServer[1] + "\n"), attrDef);
				}
			} catch (java.net.ConnectException | java.lang.StringIndexOutOfBoundsException exitExc) {
				System.out.println("\033[1;31mInterrupted: ListenerThread is not able to connect:\033[1;m " + exitExc);
				System.exit(1);
			} catch (Exception e) {
				System.out.println("\033[1;31mInterrupted:\033[1;m " + e.toString());
			}
		}
	}
}

