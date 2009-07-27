/**
 * 
 */
package ca.uhn.hunit.example;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import ca.uhn.hl7v2.app.DefaultApplication;
import ca.uhn.hl7v2.llp.MinLLPReader;
import ca.uhn.hl7v2.llp.MinLLPWriter;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.PipeParser;

public final class MllpHl7v2MessageSwapper extends Thread {
	private final String myNewValue;
	private PipeParser myParser = new PipeParser();
	private String myOldValue;
	private boolean myPrintOutput;
	private int myIterations;

	public MllpHl7v2MessageSwapper(boolean thePrintOutput, String theOldValue, String theNewValue) {
		this(thePrintOutput, theOldValue, theNewValue, 1);
	}

	public MllpHl7v2MessageSwapper(boolean thePrintOutput, String theOldValue, String theNewValue, int theIterations) {
		myPrintOutput = thePrintOutput;
		myOldValue = theOldValue;
		myNewValue = theNewValue;
		myIterations = theIterations;
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

			if (myPrintOutput) {
				System.out.println("Accepting connection from " + socket.getInetAddress().getHostAddress());
			}

			for (int i = 0; i < myIterations; i++) {

				MinLLPReader minLLPReader = new MinLLPReader(socket.getInputStream());

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

				messageText = messageText.replace(myOldValue, myNewValue);

				if (myPrintOutput) {
					System.out.println("Opening outbound connection to port " + 10200);
				}

				Socket outSocket = new Socket();
				outSocket.connect(new InetSocketAddress("localhost", 10200));

				if (myPrintOutput) {
					System.out.println("Sending message:\r\n" + messageText + "\r\n");
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
}