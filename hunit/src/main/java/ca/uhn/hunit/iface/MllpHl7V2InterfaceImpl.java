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

import ca.uhn.hunit.event.InterfaceInteractionEnum;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontSendException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.MllpHl7V2Interface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MllpHl7V2InterfaceImpl extends AbstractInterface {
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
    private String myEncoding;
    private String myIp;
    private boolean myClientMode;
    private boolean myStarted;
    private boolean myStopped;
    private int myPort;
    private final BlockingQueue<TestMessage> myReceivedMessages = new LinkedBlockingQueue<TestMessage>();
    private HL7Server myServerConnection;
    private MyClientConnectionThread myClientThread;

    //~ Constructors ---------------------------------------------------------------------------------------------------
    public MllpHl7V2InterfaceImpl(TestBatteryImpl theBattery, MllpHl7V2Interface theConfig) {
        super(theBattery, theConfig);
        myIp = theConfig.getIp();
        myPort = theConfig.getPort();
        myClientMode = theConfig.getMode().equalsIgnoreCase(CLIENT);
        myStarted = false;
        myConnectionTimeout = theConfig.getConnectionTimeoutMillis();
        myStopped = false;
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

    public String getEncoding() {
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

    @Override
    public boolean isStarted() {
        return myStarted;
    }

    @Override
    public TestMessage receiveMessage(TestImpl theTest, ExecutionContext theCtx, long theTimeout)
            throws TestFailureException {
        start(theCtx);

        theCtx.getLog().get(this).info("Waiting to receive message");

        TestMessage<Message> message = null;

        long endTime = System.currentTimeMillis() + theTimeout;

        while (!myStopped && (message == null) && (System.currentTimeMillis() < endTime)) {

            try {
                message = myReceivedMessages.poll(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
            
        }

        if (myStopped || (message == null)) {
            return null;
        }

        theCtx.getLog().get(this).info("Received message (" + message.getRawMessage().length() + " bytes)");

        return message;

    }

    @Override
    public void sendMessage(TestImpl theTest, ExecutionContext theCtx, TestMessage theMessage)
            throws TestFailureException {
        start(theCtx);

        if (theMessage.getRawMessage() == null) {
            try {
                theMessage.setRawMessage(myParser.encode((Message) theMessage.getParsedMessage()));
            } catch (HL7Exception e) {
                throw new UnexpectedTestFailureException("Can't encode message to send it: " + e.getMessage());
            }
        }

        theCtx.getLog().get(this).info("Sending message (" + theMessage.getRawMessage().length() + " bytes)");

        if (myClientMode) {
            myClientThread.sendMessageAndWaitForDelivery(theMessage);
        } else {
            // TODO: handle this by sending to a processor on the HL7 server
            throw new ConfigurationException("Sending to a server is not currently supported");
        }

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

    public void setEncoding(String myEncoding) {
        this.myEncoding = myEncoding;
    }

    public void setIp(String myIp) {
        this.myIp = myIp;
    }

    public void setPort(int myPort) {
        this.myPort = myPort;
    }

    @Override
    public void start(ExecutionContext theCtx) throws InterfaceWontStartException {
        if (myStarted) {
            return;
        }

        startInterface(theCtx);

        TestBatteryImpl battery = theCtx.getBattery();

        if (isClear() && battery.getInterfaceInteractionTypes(this).contains(InterfaceInteractionEnum.RECEIVE)) {

            long readUntil = System.currentTimeMillis() + getClearMillis();
            int cleared = 0;

            while (System.currentTimeMillis() < readUntil && !myStopped) {

                TestMessage<Message> message = null;
                try {
                    message = myReceivedMessages.poll(readUntil, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ex) {
                    // ignore
                    }

                if (message == null) {
                    continue;
                }

                cleared++;
                theCtx.getLog().get(this).info("Cleared message (" + message.getRawMessage().length() + " bytes)");

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    // nothing
                    }

                readUntil = System.currentTimeMillis() + getClearMillis();

            }

            theCtx.getLog().get(this).info("Cleared " + cleared + " messages from interface before starting");
        }

        myStarted = true;
        firePropertyChange(INTERFACE_STARTED_PROPERTY, false, true);
    }

    private void startInterface(ExecutionContext theCtx)
            throws InterfaceWontStartException {
        myClientSocket = null;
        myServerSocket = null;

        Set<InterfaceInteractionEnum> interactionTypes = theCtx.getBattery().getInterfaceInteractionTypes(this);
        // TODO: check if interaction types are appropriate

        if (myClientMode) {

            theCtx.getLog().get(this).info("Starting CLIENT interface to " + myIp + ":" + myPort);

            myClientThread = new MyClientConnectionThread();
            myClientThread.start();

        } else {

            theCtx.getLog().get(this).info("Starting SERVER interface on port " + myPort);

            try {
                myServerSocket = new ServerSocket(myPort);
                myServerSocket.setSoTimeout(250);

                myServerConnection = new HL7Server(myServerSocket, new MyApplicationRouter(), new NullSafeStorage());
                myServerConnection.start(null);
                
            } catch (IOException e) {
                throw new InterfaceWontStartException(this, e.getMessage(), e);
            }

        }

        theCtx.getLog().get(this).info("Started interface successfully");
    }

    @Override
    public void stop(ExecutionContext theCtx) throws InterfaceWontStopException {
        if (!myStarted) {
            return;
        }

        if (myStopped) {
            return;
        }

        theCtx.getLog().get(this).info("Stopping interface");

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

        myStarted = false;
        firePropertyChange(INTERFACE_STARTED_PROPERTY, true, false);
    }

    private class MyApplicationRouter extends ApplicationRouterImpl implements ReceivingApplication {

        private MyApplicationRouter() {
            super(myParser);

            bindApplication(new AppRoutingDataImpl("*", "*", "*", "*"), this);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Message processMessage(Message theMessage, Map theMetadata) throws ReceivingApplicationException, HL7Exception {
            String rawMessage = (String) theMetadata.get(RAW_MESSAGE_KEY);
            myReceivedMessages.add(new TestMessage(rawMessage, theMessage));
            try {
                return theMessage.generateACK();
            } catch (IOException ex) {
                // Presumably this will never happen as long as the message that
                // came in is valid.. but you never know
                throw new ReceivingApplicationException(ex);
            }
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
        private TestFailureException myFailure = null;

        private MyClientConnectionThread() {
            setName(getId() + "-client");
        }

        /**
         * Sends a message over the interface and waits until it has been
         * successfully delivered
         */
        public synchronized void sendMessageAndWaitForDelivery(TestMessage<Message> theMessage) throws TestFailureException {

            try {
                myMessagesToSend.put(theMessage);
            } catch (InterruptedException ex) {
                // should never happen
                throw new InterfaceWontSendException(MllpHl7V2InterfaceImpl.this, ex.getMessage());
            }

            for (;;) {
                try {
                    if (mySentMessages.take() != null) {
                        return;
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

                    }

                    myClientConnection = new Connection(myParser, new MinLowerLayerProtocol(), myClientSocket);
                    TestMessage<Message> messageToSend = myMessagesToSend.poll();
                    if (messageToSend != null) {
                        try {
                            myClientConnection.getInitiator().sendAndReceive(messageToSend.getParsedMessage());
                            mySentMessages.put(messageToSend);
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
                    continue;
                } catch (IOException ex) {
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
}
