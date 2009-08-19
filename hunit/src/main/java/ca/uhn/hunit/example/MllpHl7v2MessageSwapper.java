/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * The Initial Developer of the Original Code is University Health Network. Copyright (C)
 * 2001.  All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the  "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */
/**
 * 
 */
package ca.uhn.hunit.example;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import ca.uhn.hl7v2.app.DefaultApplication;
import ca.uhn.hl7v2.llp.MinLLPReader;
import ca.uhn.hl7v2.llp.MinLLPWriter;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.PipeParser;

public final class MllpHl7v2MessageSwapper extends Thread {
	private PipeParser myParser = new PipeParser();
	private boolean myPrintOutput;
	private int myIterations;
	private boolean myAlwaysCreateNewOutboundConnection;
	private Map<String, String> mySubstitutions;

	public MllpHl7v2MessageSwapper(boolean thePrintOutput, String theOldValue, String theNewValue) {
		this(thePrintOutput, theOldValue, theNewValue, 1);
	}

	public MllpHl7v2MessageSwapper(boolean thePrintOutput, String theOldValue, String theNewValue, int theIterations) {
		this(thePrintOutput, Collections.singletonMap(theOldValue, theNewValue), theIterations);
	}

	public MllpHl7v2MessageSwapper(boolean thePrintOutput, Map<String, String> theSubstitutions, int theIterations) {
		myPrintOutput = thePrintOutput;
		myIterations = theIterations;
		mySubstitutions = theSubstitutions;
	}

	public MllpHl7v2MessageSwapper(boolean thePrintOutput, Properties theSubstitutions, int thePasses) {
		myPrintOutput = thePrintOutput;
		myIterations = thePasses;
		mySubstitutions = new HashMap<String, String>();
		for (Map.Entry<Object, Object> next : theSubstitutions.entrySet()) {
			mySubstitutions.put(next.getKey().toString(), next.getValue().toString());
		}
	}

	@Override
	public void run() {

		Socket socket = null;
		try {
			if (myPrintOutput) {
				System.out.println("Opening server socket on port " + 10201);
			}
			ServerSocket serverSocket = new ServerSocket(10201);

			socket = serverSocket.accept();
			InputStream inputStream = socket.getInputStream();
			inputStream = new BufferedInputStream(inputStream);
			MinLLPReader minLLPReader = new MinLLPReader(inputStream);

			Socket outSocket = null;

			if (myPrintOutput) {
				System.out.println("Accepting connection from " + socket.getInetAddress().getHostAddress());
			}

			for (int i = 0; i < myIterations; i++) {

				String messageText;
				do {
					messageText = minLLPReader.getMessage();
					Thread.sleep(250);
				} while (messageText == null);

				if (myPrintOutput) {
					System.out.println("Received message:\r\n" + messageText + "\r\n");
				}

				Message replyAck = DefaultApplication.makeACK((Segment) myParser.parse(messageText).get("MSH"));
				new MinLLPWriter(socket.getOutputStream()).writeMessage(myParser.encode(replyAck));

				for (Map.Entry<String, String> next : mySubstitutions.entrySet()) {
					messageText = messageText.replace(next.getKey(), next.getValue());
				}

				if (outSocket != null && myAlwaysCreateNewOutboundConnection) {
					outSocket.close();
					outSocket = null;
				}
				
				if (outSocket == null) {
					if (myPrintOutput) {
						System.out.println("Opening outbound connection to port " + 10200);
					}
					outSocket = new Socket();
					outSocket.connect(new InetSocketAddress("localhost", 10200));
				}
				
				if (myPrintOutput) {
					System.out.println("Sending message from port " + outSocket.getLocalPort() + ":\r\n" + messageText + "\r\n");
				}

				new MinLLPWriter(outSocket.getOutputStream()).writeMessage(messageText);
				new MinLLPReader(outSocket.getInputStream()).getMessage();
			}

			serverSocket.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * If true, create a new outbound connection for each iteration
	 */
	public void setAlwaysCreateNewOutboundConnection(boolean theAlwaysCreateNewOutboundConnection) {
		myAlwaysCreateNewOutboundConnection = theAlwaysCreateNewOutboundConnection;
	}
	
	
	public static void main(String[] theArgs) {
		Options options = new Options();
		
		Option option = new Option("R", true, "Text to substiture in the message");
		option.setArgs(2);
		option.setArgName("text=substitution");
		option.setValueSeparator('=');
		option.setRequired(true);
		options.addOption(option);
		
		option = new Option("p", true, "Number of passes");
		option.setValueSeparator('=');
		option.setRequired(false);
		options.addOption(option);

		CommandLine commandLine;
		int passes;
		try {
			commandLine = new PosixParser().parse(options, theArgs);
			passes = Integer.parseInt(commandLine.getOptionValue("p", "1"));
		} catch (ParseException e) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("java -cp hunit-[version]-jar-with-dependencies.jar ca.uhn.hunit.example.MllpHl7v2MessageSwapper {-Rtext=substitution}... [options]", options);
			return;
		}
		
		Properties substitutions = commandLine.getOptionProperties("R");
		
		new MllpHl7v2MessageSwapper(true, substitutions, passes).run();
	}
	
	
}