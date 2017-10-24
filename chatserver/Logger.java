
package chatserver;

import chatserver.*;
import java.io.*;
import java.util.Date;
import java.text.*;
import static chatserver.LogLevels.*;

import java.lang.management.RuntimeMXBean;
import java.lang.management.ManagementFactory;

/*
 * class contains set of methods used for server logging
 * @method init
 * @method getProcessInfo
 * @method getCurrentTime
 * @method consoleLog
 */
public class Logger {

	private static int processPID;

	/*
	 * set of init commands to execute when server is being started
	 */
	public static void init() {
		getProcessInfo();
	}

	/*
	 * captures current process information of a running server
	 */
	public static void getProcessInfo () {
		RuntimeMXBean rMXB = ManagementFactory.getRuntimeMXBean();
		// RuntimeMXBean.getName() returns String in format <process_id>@<hostname>
		String processInfo[] = rMXB.getName().split("@", 2);
		processPID = Integer.parseInt(processInfo[0]);
		String processHostname = processInfo[1];
		consoleLog(LOG_LEVEL_INFO, "------------------------------------------------------------------------------------------------");
		consoleLog(LOG_LEVEL_INFO, "------------------------------------------------------------------------------------------------");
		consoleLog(LOG_LEVEL_INFO, "\t* Running server process hostname: " + processHostname);
		consoleLog(LOG_LEVEL_INFO, "\t* Running server process ID: " + processPID);
		consoleLog(LOG_LEVEL_INFO, "\t* Running server Java Virtual Machine implementation name: " + rMXB.getVmName());
		consoleLog(LOG_LEVEL_INFO, "\t* Running server Java Virtual Machine specification version: " + rMXB.getSpecVersion());
		consoleLog(LOG_LEVEL_INFO, "------------------------------------------------------------------------------------------------");
		consoleLog(LOG_LEVEL_INFO, "------------------------------------------------------------------------------------------------");
	}

	/*
	 * captures current time in format "dd-MM-yyyy kk:mm:ss.SSS"
	 * called each time when action is being logged by appropriate function
	 * @return String
	 */
	private static String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss.SSS");
		return sdf.format(date);
	}

	/*
	 * outputs a log message into the console with the following structure:
	 * [00-00-0000 00:00:00.000][12345][server][info ] <console log message>
	 * @param String logLevel -- any possible, examples: info/warn/error
	 * @param String logMessage -- any possible
	 */
	public static void consoleLog (String logLevel, String logMessage) {
		System.out.print("[" + getCurrentTime() + "]");
		System.out.print("[" + processPID + "]");
		System.out.print("[server]");
		System.out.print("[");
		System.out.printf("%-5s", logLevel);
		System.out.print("]");
		System.out.print(" ");
		System.out.println(logMessage);
	}
}


