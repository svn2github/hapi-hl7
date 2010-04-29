/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL
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
package ca.uhn.hunit.iface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import ca.uhn.hl7v2.protocol.impl.AppRoutingDataImpl;
import ca.uhn.hl7v2.protocol.impl.ApplicationRouterImpl;
import ca.uhn.hl7v2.protocol.impl.HL7Server;
import ca.uhn.hl7v2.protocol.impl.NullSafeStorage;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontSendException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.util.log.LogFactory;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.Hl7V2EncodingTypeEnum;
import ca.uhn.hunit.xsd.MllpHl7V2Interface;

public class MllpHl7V2InterfaceImpl extends AbstractInterface<Message> {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final String CLIENT = "client";
    private static final String SERVER = "server";

    
    //~ Instance fields ------------------------------------------------------------------------------------------------
    private Boolean myAutoAck;
    private Integer myConnectionTimeout;
//    private MinLLPReader myReader;
//    private MinLLPWriter myWriter;
    private Parser myParser;
    private ServerSocket myServerSocket;
    private Socket myClientSocket;
    private Hl7V2EncodingTypeEnum myEncoding;
    private String myIp;
    private boolean myClientMode;
    private int myPort;
    private HL7Server myServerConnection;
    private MyClientConnectionThread myClientThread;

    //~ Constructors ---------------------------------------------------------------------------------------------------
    public MllpHl7V2InterfaceImpl(TestBatteryImpl theBattery, MllpHl7V2Interface theConfig) {
        super(theBattery, theConfig);
        myIp = theConfig.getIp();
        myPort = theConfig.getPort();
        myClientMode = theConfig.getMode().equalsIgnoreCase(CLIENT);
        myConnectionTimeout = theConfig.getConnectionTimeoutMillis();
        myEncoding = theConfig.getEncoding();
        myAutoAck = theConfig.isAutoAck();

        init();
    }

    /**
     * Empty instance constructor
     */
    public MllpHl7V2InterfaceImpl(TestBatteryImpl theBattery, String theId) {
        super(theBattery, theId);

        init();
    }

    //~ Methods --------------------------------------------------------------------------------------------------------
    /**
     * Subclasses should make use of this method to export AbstractInterface properties into
     * the return value for {@link #exportConfigToXml()}
     */
    protected MllpHl7V2Interface exportConfig(MllpHl7V2Interface theConfig) {
        super.exportConfig(theConfig);
        theConfig.setAutoAck(myAutoAck);
        theConfig.setConnectionTimeoutMillis(myConnectionTimeout);
        theConfig.setEncoding(myEncoding);
        theConfig.setMode(myClientMode ? CLIENT : SERVER);
        theConfig.setIp(myIp);
        theConfig.setPort(myPort);

        return theConfig;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AnyInterface exportConfigToXml() {
        AnyInterface retVal = new AnyInterface();
        retVal.setMllpHl7V2Interface(exportConfig(new MllpHl7V2Interface()));

        return retVal;
    }

    public int getConnectionTimeout() {
        return myConnectionTimeout;
    }

    public Hl7V2EncodingTypeEnum getEncoding() {
        return myEncoding;
    }

    public String getIp() {
        return myIp;
    }

    public int getPort() {
        return myPort;
    }

    private void init() {
        if (myConnectionTimeout == null) {
            myConnectionTimeout = 10000;
        }

        if ("XML".equals(myEncoding)) {
            myParser = new DefaultXMLParser();
        } else {
            myParser = new PipeParser();
        }

        if (myAutoAck == null) {
            myAutoAck = true;
        }

        myParser.setValidationContext(new ValidationContextImpl());
    }

    public boolean isAutoAck() {
        return myAutoAck;
    }

    public boolean isClientMode() {
        return myClientMode;
    }

    public void setAutoAck(boolean myAutoAck) {
        this.myAutoAck = myAutoAck;
    }

    public void setClientMode(boolean myClientMode) {
        this.myClientMode = myClientMode;
    }

    public void setConnectionTimeout(int myConnectionTimeout) {
        this.myConnectionTimeout = myConnectionTimeout;
    }

    public void setEncoding(Hl7V2EncodingTypeEnum myEncoding) {
        this.myEncoding = myEncoding;
    }

    public void setIp(String myIp) {
        this.myIp = myIp;
    }

    public void setPort(int myPort) {
        this.myPort = myPort;
    }


    private class MyReceivingApplication extends ApplicationRouterImpl implements ReceivingApplication {

        private MyReceivingApplication() {
            super(myParser);

            bindApplication(new AppRoutingDataImpl("*", "*", "*", "*"), this);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Message processMessage(Message theMessage, Map theMetadata) throws ReceivingApplicationException, HL7Exception {
        	String rawMessage = (String) theMetadata.get(RAW_MESSAGE_KEY);
        	LogFactory.INSTANCE.get(MllpHl7V2InterfaceImpl.this).info("Received message (" + rawMessage.length() + " bytes)");
            
            TestMessage<Message> response = internalReceiveMessage(new TestMessage<Message>(rawMessage, theMessage));
            Message retVal = null;
            if (response == null) {
            	try {
					retVal = theMessage.generateACK();
				} catch (IOException e) {
	                // Presumably this will never happen as long as the message that
	                // came in is valid.. but you never know
	                throw new ReceivingApplicationException(e);
				}
            }

            if (response.getParsedMessage() == null) {
            	retVal = myParser.parse(response.getRawMessage());
            } else {
            	retVal = response.getParsedMessage();
            }
            
            return retVal;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean canProcess(Message theMessage) {
            return true;
        }
    }

    private class MyClientConnectionThread extends Thread {

        private boolean myStopped = false;
        private Connection myClientConnection;
        private BlockingQueue<TestMessage<Message>> myMessagesToSend = new LinkedBlockingQueue<TestMessage<Message>>();
        private BlockingQueue<TestMessage<Message>> mySentMessages = new LinkedBlockingQueue<TestMessage<Message>>();
        private BlockingQueue<TestMessage<Message>> myResponses = new LinkedBlockingQueue<TestMessage<Message>>();
        private TestFailureException myFailure = null;

        private MyClientConnectionThread() {
            setName(getId() + "-client");
        }

        /**
         * Sends a message over the interface and waits until it has been
         * successfully delivered
         */
        public synchronized TestMessage<Message> sendMessageAndWaitForDelivery(TestMessage<Message> theMessage) throws TestFailureException {

            try {
                myMessagesToSend.put(theMessage);
            } catch (InterruptedException ex) {
                // should never happen
                throw new InterfaceWontSendException(MllpHl7V2InterfaceImpl.this, ex.getMessage());
            }

            for (;;) {
                try {
                    if (mySentMessages.take() != null) {
                        return myResponses.take();
                    }
                    if (myFailure != null) {
                        throw myFailure;
                    }
                } catch (InterruptedException ex) {
                    // ignore
                }

            } // for
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {

            while (!myStopped) {

                try {

                    while (!myStopped && !((myClientSocket != null) && myClientSocket.isConnected())) {

                        if (myClientSocket != null) {
                            myClientSocket.close();
                        }

                        myClientSocket = new Socket();
                        myClientSocket.connect(new InetSocketAddress(myIp, myPort), 500);

                        if (myClientConnection != null) {
                        	myClientConnection.close();
                        }
                        
                        myClientConnection = new Connection(myParser, new MinLowerLayerProtocol(), myClientSocket);
                        
                    }

                    TestMessage<Message> messageToSend;
					try {
						messageToSend = myMessagesToSend.poll(5, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						messageToSend = null;
					}
					
                    if (messageToSend != null) {
                        try {
                            Message response = myClientConnection.getInitiator().sendAndReceive(messageToSend.getParsedMessage());
                            mySentMessages.put(messageToSend);
                            myResponses.put(new TestMessage<Message>(response.encode(), response));
                        } catch (InterruptedException ex) {
                            // Should never happen
                            myFailure = new InterfaceWontSendException(MllpHl7V2InterfaceImpl.this, ex.getMessage());
                            myStopped = true;
                        } catch (HL7Exception ex) {
                            myFailure = new InterfaceWontSendException(MllpHl7V2InterfaceImpl.this, ex.getMessage());
                            myStopped = true;
                        } catch (Exception ex) {
                            myFailure = new InterfaceWontSendException(MllpHl7V2InterfaceImpl.this, ex.getMessage());
                            myStopped = true;
                        }
                    }

                } catch (LLPException ex) {
                	
                	try {
                		// Yield a little bit to prevent thrash
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// ignore
					}
                	
                    continue;
                    
                } catch (IOException ex) {

                	try {
                		// Yield a little bit to prevent thrash
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// ignore
					}
                	
                	continue;
                	
                } // try-catch

            } // while

        } // method

        /**
         * Request that the connection loop be stopped
         */
        public void stopThread() {
            myStopped = true;
            synchronized (myMessagesToSend) {
                myMessagesToSend.notifyAll();
            }

            synchronized (mySentMessages) {
                mySentMessages.notifyAll();
            }
        }
    }

	@Override
	protected TestMessage<Message> internalSendMessage(TestMessage<Message> theMessage) throws TestFailureException {
        if (theMessage.getRawMessage() == null) {
            try {
                theMessage.setRawMessage(myParser.encode((Message) theMessage.getParsedMessage()));
            } catch (HL7Exception e) {
                throw new UnexpectedTestFailureException("Can't encode message to send it: " + e.getMessage());
            }
        }

        LogFactory.INSTANCE.get(this).info("Sending message (" + theMessage.getRawMessage().length() + " bytes)");

        if (myClientMode) {
            TestMessage<Message> response = myClientThread.sendMessageAndWaitForDelivery(theMessage);
        } else {
            // TODO: handle this by sending to a processor on the HL7 server
            throw new ConfigurationException("Sending to a server is not currently supported");
        }
        
		return null;
	}

	@Override
	public TestMessage<Message> generateDefaultReply(TestMessage<Message> theTestMessage) throws UnexpectedTestFailureException {
		try {
			return new TestMessage<Message>(null, theTestMessage.getParsedMessage().generateACK());
		} catch (HL7Exception e) {
			throw new UnexpectedTestFailureException(e);
		} catch (IOException e) {
			throw new UnexpectedTestFailureException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStartReceiving() throws InterfaceWontStartException {
        if (myClientMode) {
       
        	// TODO: client mode for receiving
        	throw new IllegalStateException();
        	
        } else {

            LogFactory.INSTANCE.get(this).info("Starting SERVER interface on port " + myPort);

            try {
                myServerSocket = new ServerSocket(myPort);
                myServerSocket.setSoTimeout(250);

                myServerConnection = new HL7Server(myServerSocket, new MyReceivingApplication(), new NullSafeStorage());
                myServerConnection.start(null);
                
            } catch (IOException e) {
                throw new InterfaceWontStartException(this, e.getMessage(), e);
            }

        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStart() throws InterfaceWontStartException {
		// nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStartSending() throws InterfaceWontStartException {
        if (myClientMode) {

            LogFactory.INSTANCE.get(this).info("Starting CLIENT interface to " + myIp + ":" + myPort);

            myClientThread = new MyClientConnectionThread();
            myClientThread.start();

        } else {

        	// TODO: client mode for receiving
        	throw new IllegalStateException();

        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStop() throws InterfaceWontStopException {
        try {
            if (myServerConnection != null) {
                myServerConnection.stop();
            }
            if (myServerSocket != null) {
                myServerSocket.close();
            }
            if (myClientThread != null) {
                myClientThread.stopThread();
            }
            if (myClientSocket != null) {
                myClientSocket.close();
            }

        } catch (IOException e) {
            throw new InterfaceWontStopException(this,
                    e.getMessage(), e);
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStopReceiving() throws InterfaceWontStopException {
		// nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStopSending() throws InterfaceWontStopException {
		// nothing
	}

    /**
     * {@inheritDoc }
     */
    @Override
    protected boolean getCapabilitySupportsReply() {
        return true;
    }

}
