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

import ca.uhn.hunit.event.InterfaceInteractionEnum;
import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.ex.InterfaceWontReceiveException;
import ca.uhn.hunit.ex.InterfaceWontSendException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.SendOrReceiveFailureException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.Interface;
import ca.uhn.hunit.xsd.MllpHl7V2Interface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class MllpHl7V2InterfaceImpl extends AbstractInterface {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final String CLIENT = "client";
    private static final String SERVER = "server";

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private Boolean myAutoAck;
    private Integer myConnectionTimeout;
    private MinLLPReader myReader;
    private MinLLPWriter myWriter;
    private Parser myParser;
    private ServerSocket myServerSocket;
    private Socket mySocket;
    private String myEncoding;
    private String myIp;
    private boolean myClientMode;
    private boolean myStarted;
    private boolean myStopped;
    private int myPort;

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

        String message = null;
        Message parsedMessage;

        try {
            long endTime = System.currentTimeMillis() + theTimeout;

            while (! myStopped && (message == null) && (System.currentTimeMillis() < endTime)) {
                if (! mySocket.isConnected()) {
                    theCtx.getLog().get(this).info("Socket appears to be disconnected, attempting to reconnect");
                    startInterface(theCtx);
                }

                try {
                    message = myReader.getMessage();
                } catch (SocketTimeoutException e) {
                    // ignore
                }
            }

            if (myStopped || (message == null)) {
                return null;
            }

            theCtx.getLog().get(this).info("Received message (" + message.length() + " bytes)");

            try {
                parsedMessage = myParser.parse(message);
            } catch (EncodingNotSupportedException e) {
                throw new SendOrReceiveFailureException("Encoding issue: ", e);
            } catch (HL7Exception e) {
                throw new SendOrReceiveFailureException("HL7 issue: ", e);
            }

            if (myAutoAck) {
                try {
                    Message ack = DefaultApplication.makeACK((Segment) parsedMessage.get("MSH"));
                    String reply = myParser.encode(ack);

                    theCtx.getLog().get(this).info("Sending HL7 v2 ACK (" + reply.length() + " bytes)");
                    sendMessage(theTest,
                                theCtx,
                                new TestMessage(reply));
                } catch (EncodingNotSupportedException e) {
                    throw new SendOrReceiveFailureException("Encoding issue: ", e);
                } catch (HL7Exception e) {
                    throw new SendOrReceiveFailureException("HL7 issue: ", e);
                } catch (IOException e) {
                    throw new SendOrReceiveFailureException("IO issue: ", e);
                }
            }

            return new TestMessage(myParser.encode(parsedMessage),
                                   parsedMessage);
        } catch (LLPException e) {
            throw new InterfaceWontReceiveException(this,
                                                    e.getMessage(), e);
        } catch (IOException e) {
            throw new InterfaceWontReceiveException(this,
                                                    e.getMessage(), e);
        } catch (HL7Exception e) {
            throw new InterfaceWontReceiveException(this,
                                                    e.getMessage(), e);
        }
    }

    @Override
    public void sendMessage(TestImpl theTest, ExecutionContext theCtx, TestMessage theMessage)
                     throws InterfaceException, UnexpectedTestFailureException {
        start(theCtx);

        if (theMessage.getRawMessage() == null) {
            try {
                theMessage.setRawMessage(myParser.encode((Message) theMessage.getParsedMessage()));
            } catch (HL7Exception e) {
                throw new UnexpectedTestFailureException("Can't encode message to send it: " + e.getMessage());
            }
        }

        theCtx.getLog().get(this).info("Sending message (" + theMessage.getRawMessage().length() + " bytes)");

        try {
            myWriter.writeMessage(theMessage.getRawMessage());
            theCtx.getLog().get(this).info("Sent message");
        } catch (LLPException e) {
            throw new InterfaceWontSendException(this,
                                                 e.getMessage(), e);
        } catch (IOException e) {
            throw new InterfaceWontSendException(this,
                                                 e.getMessage(), e);
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

        if (isClear() && battery.getInterfaceInteractionTypes(getId()).contains(InterfaceInteractionEnum.RECEIVE)) {
            long readUntil = System.currentTimeMillis() + getClearMillis();
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
                    Message response = DefaultApplication.makeACK((Segment) parsedMessage.get("MSH"));
                    message = myParser.encode(response);
                    myWriter.writeMessage(message);
                    cleared++;
                    theCtx.getLog().get(this).info("Cleared message");

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        // nothing
                    }

                    readUntil = System.currentTimeMillis() + getClearMillis();
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

            theCtx.getLog().get(this).info("Cleared " + cleared + " messages from interface before starting");
        }

        myStarted = true;
        firePropertyChange(INTERFACE_STARTED_PROPERTY, false, true);
    }

    private void startInterface(ExecutionContext theCtx)
                         throws InterfaceWontStartException {
        mySocket = null;
        myServerSocket = null;

        if (myClientMode) {
            theCtx.getLog().get(this).info("Starting CLIENT interface to " + myIp + ":" + myPort);

            try {
                long endTime = System.currentTimeMillis() + myConnectionTimeout;

                while (! myStopped && ! ((mySocket != null) && mySocket.isConnected()) &&
                           (System.currentTimeMillis() < endTime)) {
                    mySocket = new Socket();

                    try {
                        mySocket.connect(new InetSocketAddress(myIp, myPort),
                                         500);
                    } catch (SocketTimeoutException e) {
                        // ignore
                    }
                }

                if (myStopped) {
                    return;
                }

                if (! mySocket.isConnected()) {
                    throw new InterfaceWontStartException(this, "Could not connect to " + myIp + ":" + myPort);
                }
            } catch (IOException e) {
                throw new InterfaceWontStartException(this,
                                                      e.getMessage(), e);
            }
        } else {
            theCtx.getLog().get(this).info("Starting SERVER interface on port " + myPort);

            try {
                myServerSocket = new ServerSocket(myPort);
                myServerSocket.setSoTimeout(250);

                long endTime = System.currentTimeMillis() + myConnectionTimeout;

                while (! myStopped && (mySocket == null) && (System.currentTimeMillis() < endTime)) {
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
                throw new InterfaceWontStartException(this,
                                                      e.getMessage(), e);
            }
        }

        try {
            mySocket.setSoTimeout(250);
            myReader = new MinLLPReader(mySocket.getInputStream());
            myWriter = new MinLLPWriter(mySocket.getOutputStream());
        } catch (SocketException e) {
            throw new InterfaceWontStartException(this,
                                                  e.getMessage(), e);
        } catch (IOException e) {
            throw new InterfaceWontStartException(this,
                                                  e.getMessage(), e);
        }

        theCtx.getLog().get(this).info("Started interface successfully");
    }

    @Override
    public void stop(ExecutionContext theCtx) throws InterfaceWontStopException {
        if (! myStarted) {
            return;
        }

        if (myStopped) {
            return;
        }

        theCtx.getLog().get(this).info("Stopping interface");

        try {
            if (myServerSocket != null) {
                myServerSocket.close();
            }

            if (mySocket != null) {
                mySocket.close();
            }
        } catch (IOException e) {
            throw new InterfaceWontStopException(this,
                                                 e.getMessage(), e);
        }

        myStarted = false;
        firePropertyChange(INTERFACE_STARTED_PROPERTY, true, false);
    }
}
