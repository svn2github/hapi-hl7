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

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.run.IExecutionContext;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.Event;
import ca.uhn.hunit.xsd.HL7V2ExpectAbstract;
import java.beans.PropertyVetoException;

public abstract class AbstractHl7V2ExpectMessage extends AbstractExpectMessage<Hl7V2MessageImpl> {
    public static final String REPLY_MESSAGE_ID_PROPERTY = "AHEM_REPLY_MESSAGE_ID_PROPERTY";


    private Hl7V2MessageImpl myReplyMessage;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public AbstractHl7V2ExpectMessage(TestImpl theTest, HL7V2ExpectAbstract theConfig)
                               throws ConfigurationException {
        super(theTest, theConfig);

        try {
            setReplyMessageId(theConfig.getReplyMessage());
        } catch (PropertyVetoException ex) {
            throw new ConfigurationException("Setting reply message ID for expect event failed with message: " + ex.getMessage());
        }

    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    protected Event exportConfig(HL7V2ExpectAbstract theConfig) {
        super.exportConfig(theConfig);

        if (myReplyMessage != null) {
            theConfig.setReplyMessage(myReplyMessage.getId());
        }

        return theConfig;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected Hl7V2MessageImpl getReplyMessage() {
        if (myReplyMessage != null) {
            return myReplyMessage;
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void receiveMessage(IExecutionContext theCtx, TestMessage<?> theMessage)
                        throws TestFailureException {
        // TODO: make sure this is sound
        TestMessage<Message> testMessage = (TestMessage<Message>) theMessage;

        validateMessage(testMessage);
    }

    public abstract void validateMessage(TestMessage<Message> theMessage)
                                  throws TestFailureException;

    /**
     * Sets the ID for the message, if any, that will be used as a reply
     */
    public void setReplyMessageId(String theMessageId) throws PropertyVetoException {
        String oldValue = (myReplyMessage != null) ? myReplyMessage.getId() : null;
        fireVetoableChange(REPLY_MESSAGE_ID_PROPERTY, oldValue, theMessageId);

        try {
            if (theMessageId == null) {
                myReplyMessage = null;
            } else {
                myReplyMessage = (Hl7V2MessageImpl) getBattery().getMessage(theMessageId);
            }
        } catch (ConfigurationException ex) {
            throw new PropertyVetoException("Unknown message ID - " + theMessageId, null);
        }

        firePropertyChange(REPLY_MESSAGE_ID_PROPERTY, oldValue, theMessageId);
    }

}
