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
package ca.uhn.hunit.event.expect;

import java.beans.PropertyVetoException;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontReceiveException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.run.IExecutionContext;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.Event;
import ca.uhn.hunit.xsd.ExpectMessage;
import ca.uhn.hunit.xsd.ExpectMessageAny;

public abstract class AbstractExpectMessage<T extends AbstractMessage<?>> extends AbstractExpect {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    @Deprecated
    public static final String MESSAGE_ID_PROPERTY = "AEM_MESSAGE_ID_PROPERTY";

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private T myMessage;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public AbstractExpectMessage(TestImpl theTest, ExpectMessage theConfig)
                          throws ConfigurationException {
        super(theTest, theConfig);

        try {
            setMessageId(theConfig.getMessageId());
        } catch (PropertyVetoException ex) {
            throw new ConfigurationException(ex.getMessage());
        }
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    @Override
    public void execute(IExecutionContext theCtx) throws TestFailureException, ConfigurationException {
        final T replyMessage = getReplyMessage();
        final TestMessage<?> testMessage = replyMessage != null ? replyMessage.getTestMessage() : null;
        
        TestMessage<T> message = getInterface().receiveMessage(getReceiveTimeout(),testMessage);

        if (! getInterface().isStarted()) {
            return;
        }

        if (message == null) {
            throw new InterfaceWontReceiveException(getInterface(),
                                                    "Didn't receive a message after " + getReceiveTimeout() + "ms");
        }

        receiveMessage(theCtx, message);
    }

    
    public Event exportConfig(ExpectMessage theConfig) {
        super.exportConfig(theConfig);
        return theConfig;
    }


    /**
     * Subclasses may override this method to provide the reply message which will be supplied
     * when a message is received.
     */
    protected T getReplyMessage() {
        return null;
    }

    /**
     * Overriding to provide a specific type requirement
     */
    @Override
    public abstract ExpectMessageAny exportConfigToXmlAndEncapsulate();

    /**
     * {@inheritDoc }
     */
    @Deprecated
    public T provideLinkedMessage() {
        return myMessage;
    }

    public abstract void receiveMessage(IExecutionContext theCtx, TestMessage<?> theMessage)
                                 throws TestFailureException;

    /**
     * {@inheritDoc }
     */
    @Deprecated
    public void setMessageId(String theMessageId) throws PropertyVetoException {
        String oldValue = (myMessage != null) ? myMessage.getId() : null;
        fireVetoableChange(MESSAGE_ID_PROPERTY, oldValue, theMessageId);

        try {
            if (theMessageId == null) {
                myMessage = null;
            } else {
                myMessage = (T) getBattery().getMessage(theMessageId);
            }
        } catch (ConfigurationException ex) {
            throw new PropertyVetoException("Unknown message ID", null);
        }

        firePropertyChange(MESSAGE_ID_PROPERTY, oldValue, theMessageId);
    }
}
