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

import java.beans.PropertyVetoException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.run.IExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.util.AbstractModelClass;
import ca.uhn.hunit.util.log.LogFactory;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.Interface;

/**
 * Base class for an interface
 * 
 * @param <T>
 *            The type of message class this interface is able to send and
 *            receive
 */
public abstract class AbstractInterface<T> extends AbstractModelClass implements Comparable<AbstractInterface<?>> {
    // ~ Static fields/initializers
    // -------------------------------------------------------------------------------------

    
    public static final String INTERFACE_STARTED_PROPERTY = "INTERFACE_STARTED_PROPERTY";
    public static final String INTERFACE_ID_PROPERTY = "INTERFACE_ID_PROPERTY";

    // ~ Instance fields
    // ------------------------------------------------------------------------------------------------

    private Boolean myAutostart;
    private Boolean myClear;
    private Integer myClearMillis;
    private String myId;
    private final TestBatteryImpl myBattery;
    private final LinkedBlockingQueue<TestMessage<T>> mySendMessage = new LinkedBlockingQueue<TestMessage<T>>();
    private final LinkedBlockingQueue<TestMessage<T>> myReceiveMessage = new LinkedBlockingQueue<TestMessage<T>>();
    private final LinkedBlockingQueue<TestMessage<T>> myReplyMessage = new LinkedBlockingQueue<TestMessage<T>>();
    private boolean myStartedForReceiving;
    private boolean myStartedForSending;

    // ~ Constructors
    // ---------------------------------------------------------------------------------------------------

    /**
     * Constructor
     */
    public AbstractInterface(TestBatteryImpl theBattery, Interface theConfig) {
        myBattery = theBattery;
        myId = theConfig.getId();
        myAutostart = theConfig.isAutostart();
        myClearMillis = theConfig.getClearMillis();
        myClear = theConfig.isClear();

        init();
    }

    /**
     * Constructor
     */
    public AbstractInterface(TestBatteryImpl theBattery, String theId) {
        myBattery = theBattery;
        myId = theId;

        init();
    }

    // ~ Methods
    // --------------------------------------------------------------------------------------------------------

    public int compareTo(AbstractInterface<?> theO) {
        return myId.compareTo(theO.myId);
    }

    /**
     * Subclasses should make use of this method to export AbstractInterface
     * properties into the return value for {@link #exportConfigToXml()}
     */
    protected Interface exportConfig(Interface theConfig) {
        theConfig.setAutostart(myAutostart);
        theConfig.setId(myId);
        theConfig.setClearMillis(myClearMillis);
        theConfig.setClear(myClear);

        return theConfig;
    }

    /**
     * Declare a concrete type for subclass implementations of this method
     */
    @Override
    public abstract AnyInterface exportConfigToXml();

    public int getClearMillis() {
        return myClearMillis;
    }

    public String getId() {
        return myId;
    }

    private void init() {
        if (myAutostart == null) {
            myAutostart = true;
        }

        if (myClearMillis == null) {
            myClearMillis = 100;
        }

        if (myClear == null) {
            myClear = true;
        }
    }

    public boolean isAutostart() {
        return myAutostart;
    }

    public boolean isClear() {
        return myClear;
    }

    /**
     * Returns true if this interface is started for either sending or receiving
     */
    public final boolean isStarted() {
        return myStartedForSending || myStartedForReceiving;
    }

    public TestMessage<T> receiveMessage(long theTimeout, TestMessage<T> theReply) throws TestFailureException {
        start(false, true);

        TestMessage<T> receivedMessage = null;
        long waitUntil = System.currentTimeMillis() + theTimeout;
        while (System.currentTimeMillis() < waitUntil && receivedMessage == null) {
            try {
                receivedMessage = myReceiveMessage.poll(waitUntil - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        if (receivedMessage == null) {
            return null;
        }

        if (isProducesReply()) {
            if (theReply == null) {
                theReply = generateDefaultReply(receivedMessage);
            }

            try {
                myReplyMessage.put(theReply);
            } catch (InterruptedException e) {
                throw new UnexpectedTestFailureException(e);
            }
        }

        return receivedMessage;
    }

    public abstract TestMessage<T> generateDefaultReply(TestMessage<T> theTestMessage) throws TestFailureException;

    public void setAutostart(boolean theAutostart) {
        myAutostart = theAutostart;
    }

    public void setClear(boolean theClear) {
        myClear = theClear;
    }

    public void setClearMillis(int theClearMillis) {
        myClearMillis = theClearMillis;
    }

    public void setId(String theId) throws PropertyVetoException {
        if (StringUtils.equals(theId, myId)) {
            return;
        }

        if (StringUtils.isEmpty(theId)) {
            throw new PropertyVetoException(Strings.getInstance().getString("interface.id.empty"), null);
        }

        if (myBattery.getInterfaceIds().contains(theId)) {
            throw new PropertyVetoException(Strings.getInstance().getString("interface.id.duplicate"), null);
        }

        String oldValue = myId;
        firePropertyChange(INTERFACE_ID_PROPERTY, oldValue, theId);
        myId = theId;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractInterface<?> other = (AbstractInterface<?>) obj;
        if ((this.myId == null) ? (other.myId != null) : !this.myId.equals(other.myId)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.myId != null ? this.myId.hashCode() : 0);
        return hash;
    }

    /**
     * Send a message to this interface, either without expecting or ignoring
     * any response. The appropriate behaviour (not expect / ignore) is
     * determined by the specific type of interface this is.
     */
    public void sendMessageOnly(IExecutionContext theCtx, TestMessage<T> theMessage) throws TestFailureException {
        start(true, false);
        internalSendMessage(theMessage);
    }

    /**
     * Subclasses must override this method to send a message
     * 
     * @param theMessage
     *            The message to send
     * @throws TestFailureException
     */
    protected abstract TestMessage<T> internalSendMessage(TestMessage<T> theMessage) throws TestFailureException;

    /**
     * Subclasses must invoke this method when receiving a message to provide
     * the message that was received. This method will provide the response, if
     * any
     * 
     * @param theMessage
     *            The message received
     * @return The response message
     */
    protected TestMessage<T> internalReceiveMessage(TestMessage<T> theMessage) {
        myReceiveMessage.add(theMessage);
        while (isStarted() && isProducesReply()) {
            try {
                TestMessage<T> take = myReplyMessage.take();
                return take;
            } catch (InterruptedException e) {
                // nothing
            }
        }

        return null;
    }

    /**
     * Defaults to true, subclasses may override to indicate that they don't
     * produce message replies
     */
    protected boolean isProducesReply() {
        return true;
    }

    public void stop() throws InterfaceWontStopException {
        if (!isStarted()) {
            return;
        }

        LogFactory.INSTANCE.get(this).info("Stopping interface");

        if (myStartedForReceiving) {
            doStopReceiving();
            myStartedForReceiving = false;
        }

        if (myStartedForSending) {
            doStopSending();
            myStartedForSending = false;
        }

        doStop();

        firePropertyChange(INTERFACE_STARTED_PROPERTY, true, false);
    }

    public void start(boolean theForSending, boolean theForReceiving) throws InterfaceWontStartException {
        // TODO: check if we're started for the right activity
        if (isStarted()) {
            return;
        }

        doStart();

        if (theForSending) {
            doStartSending();
        }
        if (theForReceiving) {
            doStartReceiving();
        }

        myStartedForReceiving = theForReceiving;
        myStartedForSending = theForSending;

        if (isClear()) {
            long readUntil = System.currentTimeMillis() + getClearMillis();
            int cleared = 0;

            while (System.currentTimeMillis() < readUntil) {
                try {
                    TestMessage<T> message = receiveMessage(getClearMillis(), null);

                    if ((message == null) || (message.getRawMessage().length() == 0)) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            // nothing
                        }

                        continue;
                    }

                    cleared++;
                    LogFactory.INSTANCE.get(this).info("Cleared message");

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        // nothing
                    }

                    readUntil = System.currentTimeMillis() + getClearMillis();
                } catch (TestFailureException e) {
                    LogFactory.INSTANCE.get(this).warn("Error while clearing queue: " + e.getMessage());
                }
            }

            LogFactory.INSTANCE.get(this).info("Cleared " + cleared + " messages from interface before starting");
        }

        firePropertyChange(INTERFACE_STARTED_PROPERTY, false, true);
        LogFactory.INSTANCE.get(this).info("Started interface successfully");
    }

    /**
     * Starts the interface receiving messages
     */
    protected abstract void doStartReceiving() throws InterfaceWontStartException;

    /**
     * Starts the interface sending messages
     */
    protected abstract void doStartSending() throws InterfaceWontStartException;

    /**
     * Starts the interface sending messages
     */
    protected abstract void doStart() throws InterfaceWontStartException;

    /**
     * Stops the interface
     */
    protected abstract void doStop() throws InterfaceWontStopException;

    /**
     * Stops the interface
     */
    protected abstract void doStopReceiving() throws InterfaceWontStopException;

    /**
     * Stops the interface
     */
    protected abstract void doStopSending() throws InterfaceWontStopException;

    /**
     * Does this interface support replying to an incoming message, or receiving
     * a reply to an outgoing message?
     */
    protected abstract boolean getCapabilitySupportsReply();

    /**
     * @return Returns true. Specific interface implementations may override if they
     * want to specify they they do not support "clear" (waiting for stale messages to
     * drain from the interface before starting)
     */
    public boolean isSupportsClear() {
        return true;
    }

}
