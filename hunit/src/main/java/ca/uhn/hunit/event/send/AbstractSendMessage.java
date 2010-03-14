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
package ca.uhn.hunit.event.send;

import java.beans.PropertyVetoException;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.event.InterfaceInteractionEnum;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.run.IExecutionContext;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.SendMessage;
import ca.uhn.hunit.xsd.SendMessageAny;

public abstract class AbstractSendMessage<V, T extends AbstractMessage<V>> extends AbstractEvent {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private T myMessage;

    //~ Constructors ---------------------------------------------------------------------------------------------------
    public AbstractSendMessage(TestImpl theTest, SendMessage theConfig)
            throws ConfigurationException {
        super(theTest, theConfig);

        String messageId = theConfig.getMessageId();

        if (messageId != null) {
            myMessage = (T) theTest.getBattery().getMessage(messageId);
        }
    }

    //~ Methods --------------------------------------------------------------------------------------------------------
    @Override
    public void execute(IExecutionContext theCtx) throws TestFailureException {

        TestMessage<V> message = provideMessage().getTestMessage();

        message = massageMessage(message);

        getInterface().sendMessageOnly(theCtx, message);
    }

    /**
     * Subclasses should implement this method to provide the message they are sending
     */
    protected abstract AbstractMessage<V> provideMessage();
    
    public SendMessage exportConfig(SendMessage theConfig) {
        super.exportConfig(theConfig);
        return theConfig;
    }

    /**
     * Overriding to provide a specific type requirement
     */
    @Override
    public abstract SendMessage exportConfigToXml();

    /**
     * Overriding to provide a specific type requirement
     */
    public abstract SendMessageAny exportConfigToXmlAndEncapsulate();

    /**
     * {@inheritDoc }
     */
    @Override
    public InterfaceInteractionEnum getInteractionType() {
        return InterfaceInteractionEnum.SEND;
    }

    @Deprecated
    public AbstractMessage<?> getMessage() {
        return myMessage;
    }

    /**
     * Subclasses should override this method to perform any message massaging
     * they need to do. It is ok to just return the object passed in.
     */
    public abstract TestMessage<V> massageMessage(TestMessage<V> theInput)
            throws TestFailureException;


//    public void setMessageId(String theMessageId) throws PropertyVetoException {
//        T newMessage;
//
//        try {
//            newMessage = (theMessageId != null) ? (T) getBattery().getMessage(theMessageId) : null;
//        } catch (ConfigurationException ex) {
//            throw new PropertyVetoException(ex.getMessage(), null);
//        }
//
//        String oldValue = (myMessage != null) ? myMessage.getId() : null;
//        fireVetoableChange(MESSAGE_ID_PROPERTY, oldValue, theMessageId);
//        this.myMessage = newMessage;
//        firePropertyChange(MESSAGE_ID_PROPERTY, oldValue, theMessageId);
//    }
}
