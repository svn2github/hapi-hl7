package ca.uhn.hunit.iface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.DefaultApplication;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLLPReader;
import ca.uhn.hl7v2.llp.MinLLPWriter;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.ex.InterfaceWontReceiveException;
import ca.uhn.hunit.ex.InterfaceWontSendException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.MllpHl7V2Interface;

public class MllpHl7V2InterfaceImpl extends AbstractInterface {

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
	private boolean myStopped;
	private Parser myParser;
	private Boolean myAutoAck;
	private Integer myClearMillis;

	public MllpHl7V2InterfaceImpl(MllpHl7V2Interface theConfig) {
		super(theConfig);
		myIp = theConfig.getIp();
		myPort = theConfig.getPort();
		myClientMode = theConfig.getMode().equalsIgnoreCase("client");
		myStarted = false;
		myConnectionTimeout = theConfig.getConnectionTimeoutMillis();
		myReceiveTimeout = theConfig.getReceiveTimeoutMillis();
		myStopped = false;
		myClearMillis = theConfig.getClearMillis();
		
		if (myConnectionTimeout == null) {
			myConnectionTimeout = 10000;
		}
		if (myReceiveTimeout == null) {
			myReceiveTimeout = 10000;
		}
		if ("XML".equals(theConfig.getEncoding())) {
			myParser = new DefaultXMLParser();
		} else {
			myParser = new PipeParser();
		}
		myParser.setValidationContext(new ValidationContextImpl());

		myAutoAck = theConfig.isAutoAck();
		
		if (myAutoAck == null) {
			myAutoAck = true;
		}

	}

	@Override
	public TestMessage receiveMessage(TestImpl theTest, ExecutionContext theCtx) throws TestFailureException {
		start(theCtx);

		theCtx.getLog().info(this, "Waiting to receive message");

		String message = null;
		Message parsedMessage;
		try {
			long endTime = System.currentTimeMillis() + myReceiveTimeout;
			while (!myStopped && message == null && System.currentTimeMillis() < endTime) {
				if (!mySocket.isConnected()) {
					theCtx.getLog().info(this, "Socket appears to be disconnected, attempting to reconnect");
					startInterface(theCtx);
				}
				
				try {
					message = myReader.getMessage();
				} catch (SocketTimeoutException e) {
					// ignore
				}
			}
			if (myStopped) {
				return null;
			}
			
			if (message == null) {
				throw new InterfaceWontReceiveException(this, "Didn't receive a message after " + myReceiveTimeout + "ms");
			}
			
			theCtx.getLog().info(this, "Received message (" + message.length() + " bytes)");

			try {
				parsedMessage = myParser.parse(message);
			} catch (EncodingNotSupportedException e) {
				throw new IncorrectMessageReceivedException(theTest, message, e.getMessage());
			} catch (HL7Exception e) {
				throw new IncorrectMessageReceivedException(theTest, message, e.getMessage());
			}

			if (myAutoAck) {
				try {
					Message ack = DefaultApplication.makeACK((Segment) parsedMessage.get("MSH"));
					String reply = myParser.encode(ack);

					theCtx.getLog().info(this, "Sending HL7 v2 ACK (" + reply.length() + " bytes)");
					sendMessage(theTest, theCtx, new TestMessage(reply));
				} catch (EncodingNotSupportedException e) {
					throw new IncorrectMessageReceivedException(theTest, e, message, "Problem generating ACK - " + e.getMessage());
				} catch (HL7Exception e) {
					throw new IncorrectMessageReceivedException(theTest, e, message, "Problem generating ACK - " + e.getMessage());
				} catch (IOException e) {
					throw new IncorrectMessageReceivedException(theTest, e, message, "Problem generating ACK - " + e.getMessage());
				}
				
			}

		     return new TestMessage(myParser.encode(parsedMessage), parsedMessage);

		} catch (LLPException e) {
			throw new InterfaceWontReceiveException(this, e.getMessage(), e);
		} catch (IOException e) {
			throw new InterfaceWontReceiveException(this, e.getMessage(), e);
		} catch (HL7Exception e) {
            throw new InterfaceWontReceiveException(this, e.getMessage(), e);
        }

	}

	@Override
	public void sendMessage(TestImpl theTest, ExecutionContext theCtx, TestMessage theMessage) throws InterfaceException, UnexpectedTestFailureException {
		start(theCtx);

		if (theMessage.getRawMessage() == null) {
			try {
				theMessage.setRawMessage(myParser.encode((Message) theMessage.getParsedMessage()));
			} catch (HL7Exception e) {
				throw new UnexpectedTestFailureException("Can't encode message to send it: " + e.getMessage());
			}
		}
		
		theCtx.getLog().info(this, "Sending message (" + theMessage.getRawMessage().length() + " bytes)");

		try {
			myWriter.writeMessage(theMessage.getRawMessage());
			theCtx.getLog().info(this, "Sent message");
		} catch (LLPException e) {
			throw new InterfaceWontSendException(this, e.getMessage(), e);
		} catch (IOException e) {
			throw new InterfaceWontSendException(this, e.getMessage(), e);
		}

	}

	@Override
	public void start(ExecutionContext theCtx) throws InterfaceWontStartException {
		if (myStarted) {
			return;
		}

		startInterface(theCtx);

		if (myClearMillis != null) {
			long readUntil = System.currentTimeMillis() + myClearMillis;
			int cleared = 0;
			while (System.currentTimeMillis() < readUntil) {
				try {
					String message = myReader.getMessage();
					if (message == null) {
					    try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            // nothing
                        }
                        continue;
					}
					Message parsedMessage = myParser.parse(message);
					Message response = DefaultApplication.makeACK((Segment)parsedMessage.get("MSH"));
					message = myParser.encode(response);
					myWriter.writeMessage(message);
					cleared++;
					theCtx.getLog().info(this, "Cleared message");
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        // nothing
                    }
                    readUntil = System.currentTimeMillis() + myClearMillis;
				} catch (LLPException e) {
					// ignore
				} catch (IOException e) {
					break;
				} catch (EncodingNotSupportedException e) {
                    // ignore
                } catch (HL7Exception e) {
                    // ignore
                }
			}
			theCtx.getLog().info(this, "Cleared " + cleared + " messages from interface before starting");			
		}
		
		myStarted = true;
	}

	private void startInterface(ExecutionContext theCtx) throws InterfaceWontStartException {
		mySocket = null;
		myServerSocket = null;
		
		if (myClientMode) {
			theCtx.getLog().info(this, "Starting CLIENT interface to " + myIp + ":" + myPort);
			try {

				long endTime = System.currentTimeMillis() + myReceiveTimeout;
				while (!myStopped && !(mySocket != null && mySocket.isConnected()) && System.currentTimeMillis() < endTime) {
					mySocket = new Socket();
					try {
						mySocket.connect(new InetSocketAddress(myIp, myPort), 500);
					} catch (SocketTimeoutException e) {
						// ignore
					}
				}
				if (myStopped) {
					return;
				}

				if (!mySocket.isConnected()) {
					throw new InterfaceWontStartException(this, "Could not connect to " + myIp + ":" + myPort);
				}
				
			} catch (IOException e) {
				throw new InterfaceWontStartException(this, e.getMessage(), e);
			}
		} else {
			theCtx.getLog().info(this, "Starting SERVER interface on port " + myPort);
			try {
				myServerSocket = new ServerSocket(myPort);
				myServerSocket.setSoTimeout(250);
				
				long endTime = System.currentTimeMillis() + myReceiveTimeout;
				while (!myStopped && mySocket == null && System.currentTimeMillis() < endTime) {
					try {
						mySocket = myServerSocket.accept();
					} catch (SocketTimeoutException e) {
						// ignore
					}
				}
				
				if (mySocket == null) {
					throw new InterfaceWontStartException(this, "Timed out waiting for connection on port " + myPort);
				}
				
			} catch (IOException e) {
				throw new InterfaceWontStartException(this, e.getMessage(), e);
			}
		}

		try {
			mySocket.setSoTimeout(250);
			myReader = new MinLLPReader(mySocket.getInputStream());
			myWriter = new MinLLPWriter(mySocket.getOutputStream());
		} catch (SocketException e) {
			throw new InterfaceWontStartException(this, e.getMessage(), e);
		} catch (IOException e) {
			throw new InterfaceWontStartException(this, e.getMessage(), e);
		}
		
		theCtx.getLog().info(this, "Started interface successfully");		
	}

	@Override
	public void stop(ExecutionContext theCtx) throws InterfaceWontStopException {
		if (!myStarted) {
			return;
		}
		if (myStopped) {
			return;
		}

		theCtx.getLog().info(this, "Stopping interface");

		try {
			if (myServerSocket != null) {
				myServerSocket.close();
			}
			if (mySocket != null) {
				mySocket.close();
			}
		} catch (IOException e) {
			throw new InterfaceWontStopException(this, e.getMessage(), e);
		}

		myStarted = false;
	}

	@Override
	public boolean isStarted() {
		return myStarted;
	}

}
