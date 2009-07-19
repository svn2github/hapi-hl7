package ca.uhn.hunit.iface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLLPReader;
import ca.uhn.hl7v2.llp.MinLLPWriter;
import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.ex.InterfaceWontReceiveException;
import ca.uhn.hunit.ex.InterfaceWontSendException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.MllpInterface;

public class MllpInterfaceImpl extends AbstractInterface {

	private String myIp;
	private int myPort;
	private boolean myClientMode;
	private boolean myStarted;
	private Socket mySocket;
	private Integer myConnectionTimeout;
	private ServerSocket myServerSocket;
	private MinLLPReader myReader;
	private MinLLPWriter myWriter;
	private Integer myReceiveTimeout;

	public MllpInterfaceImpl(MllpInterface theConfig) {
		super(theConfig);
		myIp = theConfig.getIp();
		myPort = theConfig.getPort();
		myClientMode = theConfig.getMode().equalsIgnoreCase("client");
		myStarted = false;
		myConnectionTimeout = theConfig.getConnectionTimeoutMillis();
		myReceiveTimeout = theConfig.getReceiveTimeoutMillis();
		
		if (myConnectionTimeout == null) {
			myConnectionTimeout = 10000;
		}
		if (myReceiveTimeout == null) {
			myReceiveTimeout = 10000;
		}
	}

	@Override
	public String receiveMessage(ExecutionContext theCtx) throws InterfaceException {
		start(theCtx);
		
		theCtx.getLog().info(this, "Waiting to receive message");
		
		try {
			String message = myReader.getMessage();

			theCtx.getLog().info(this, "Received message (" + message.length() + " bytes)");

			return message;
		} catch (LLPException e) {
			throw new InterfaceWontReceiveException(this, e);
		} catch (IOException e) {
			throw new InterfaceWontReceiveException(this, e);
		}
	}

	@Override
	public void sendMessage(ExecutionContext theCtx, String theMessage) throws InterfaceException {
		start(theCtx);

		theCtx.getLog().info(this, "Sending message (" + theMessage.length() + " bytes)");
		
		try {
			myWriter.writeMessage(theMessage);
			theCtx.getLog().info(this, "Sent message");
		} catch (LLPException e) {
			throw new InterfaceWontSendException(this, e);
		} catch (IOException e) {
			throw new InterfaceWontSendException(this, e);
		}
		
	}

	@Override
	public void start(ExecutionContext theCtx) throws InterfaceWontStartException {
		if (myStarted) {
			return;
		}
				
		if (myClientMode) {
			theCtx.getLog().info(this, "Starting CLIENT interface to " + myIp + ":" + myPort);
			mySocket = new Socket();
			try {
				mySocket.connect(new InetSocketAddress(myIp, myPort), myConnectionTimeout);
			} catch (IOException e) {
				throw new InterfaceWontStartException(this, e);
			}
		} else {
			theCtx.getLog().info(this, "Starting SERVER interface on port " + myPort);
			try {
				myServerSocket = new ServerSocket(myPort);
				myServerSocket.setSoTimeout(myConnectionTimeout);
				mySocket = myServerSocket.accept();				
			} catch (IOException e) {
				throw new InterfaceWontStartException(this, e);
			}
		}
		
		try {
			mySocket.setSoTimeout(myReceiveTimeout);
			myReader = new MinLLPReader(mySocket.getInputStream());
			myWriter = new MinLLPWriter(mySocket.getOutputStream());
		} catch (SocketException e) {
			throw new InterfaceWontStartException(this, e);
		} catch (IOException e) {
			throw new InterfaceWontStartException(this, e);
		}
		
		theCtx.getLog().info(this, "Started interface successfully");
		myStarted = true;
	}

	@Override
	public void stop(ExecutionContext theCtx) {
		if (!myStarted) {
			return;
		}

		theCtx.getLog().info(this, "Stopping interface");

		try {
			if (myServerSocket != null) {
				myServerSocket.close();
			}
			mySocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		myStarted = false;
	}

}
