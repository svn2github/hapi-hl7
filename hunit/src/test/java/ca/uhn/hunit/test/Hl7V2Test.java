package ca.uhn.hunit.test;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import ca.uhn.hl7v2.app.DefaultApplication;
import ca.uhn.hl7v2.llp.MinLLPReader;
import ca.uhn.hl7v2.llp.MinLLPWriter;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.run.ExecutionContext;


public class Hl7V2Test {

	@Test
	public void testSuccessfulExpectSpecific() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
		startMessageReplacer("TEST");
		
		File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_tests_hl7.xml").toURI());
		TestBatteryImpl battery = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext();
		battery.execute(ctx, "ExpectSpecific Test");
		
		Assert.assertFalse(ctx.getBatteryFailures().containsKey(battery.getTestNames2Tests().get("ExpectSpecific Test")));
		Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestNames2Tests().get("ExpectSpecific Test")));
	}

	@Test
	public void testFailureExpectSpecific() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
		startMessageReplacer("TEST2");
		
		File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_tests_hl7.xml").toURI());
		TestBatteryImpl battery = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext();
		battery.execute(ctx, "ExpectSpecific Test");
		
		ITest test = battery.getTestNames2Tests().get("ExpectSpecific Test");
		Assert.assertFalse(ctx.getTestSuccesses().contains(test));
		Assert.assertTrue(ctx.getTestFailures().containsKey(test));
	}

	public static void startMessageReplacer(final String theNewValue) {
		new Thread() {

			private PipeParser myParser = new PipeParser();
			
			@Override
			public void run() {
				
				Socket socket;
				try {
					ServerSocket serverSocket = new ServerSocket(10201);
					socket = serverSocket.accept();
					MinLLPReader minLLPReader = new MinLLPReader(socket.getInputStream());
					
					String messageText;
					do {
						messageText = minLLPReader.getMessage();
						Thread.sleep(250);
					} while (messageText == null);
					
					Message replyAck = DefaultApplication.makeACK((Segment) myParser.parse(messageText).get("MSH"));
					new MinLLPWriter(socket.getOutputStream()).writeMessage(myParser.encode(replyAck));
					
					messageText = messageText.replace("LEIGHTON", theNewValue);
					
					Socket outSocket = new Socket();
					outSocket.connect(new InetSocketAddress("localhost", 10200));
					new MinLLPWriter(outSocket.getOutputStream()).writeMessage(messageText);
					new MinLLPReader(outSocket.getInputStream()).getMessage();
					
					serverSocket.close();
					socket.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}}.start();
	}
	
}
