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
import ca.uhn.hunit.compare.hl7v2.Hl7V2MessageCompare;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.ExpectMessageAny;
import ca.uhn.hunit.xsd.Hl7V2ExpectSpecificMessage;
import ca.uhn.hunit.xsd.Hl7V2MessageDefinition;
import java.util.LinkedHashMap;

public class Hl7V2ExpectSpecificMessageImpl extends AbstractHl7V2ExpectMessage {

	private Hl7V2MessageImpl myMessage;

	// ~ Constructors
	// ---------------------------------------------------------------------------------------------------

	public Hl7V2ExpectSpecificMessageImpl(TestImpl theTest, Hl7V2ExpectSpecificMessage theConfig) throws ConfigurationException {
		super(theTest, theConfig);

		Hl7V2MessageDefinition configMessage = theConfig.getMessage();
		if (configMessage != null) {
			myMessage = new Hl7V2MessageImpl(configMessage);
		} else {
			myMessage = super.provideLinkedMessage();
		}

        if (myMessage == null) {
            throw new ConfigurationException("Event has no message attached");
        }
	}

	// ~ Methods
	// --------------------------------------------------------------------------------------------------------

	public Hl7V2ExpectSpecificMessage exportConfig(Hl7V2ExpectSpecificMessage theConfig) {
		if (theConfig == null) {
			theConfig = new Hl7V2ExpectSpecificMessage();
		}

		super.exportConfig(theConfig);

		return theConfig;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public Hl7V2ExpectSpecificMessage exportConfigToXml() {
		Hl7V2ExpectSpecificMessage retVal = exportConfig(new Hl7V2ExpectSpecificMessage());
		retVal.setMessage(myMessage.exportConfigToXml());
		return retVal;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public ExpectMessageAny exportConfigToXmlAndEncapsulate() {
		ExpectMessageAny retVal = new ExpectMessageAny();
		retVal.setHl7V2Specific(exportConfigToXml());

		return retVal;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void validateMessage(TestMessage<Message> theMessage) throws TestFailureException {

		Hl7V2MessageCompare messageCompare = new Hl7V2MessageCompare();
		messageCompare.compare(myMessage.getTestMessage().getParsedMessage(), theMessage.getParsedMessage());

		if (!messageCompare.isSame()) {
			throw new IncorrectMessageReceivedException(getTest(), null, myMessage.getTestMessage(), theMessage, "Messages did not match", messageCompare);
		}
	}

    /**
     * Returns the message being expected
     */
    public Hl7V2MessageImpl getMessage() {
        return myMessage;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public LinkedHashMap<String, AbstractMessage<?>> getAllMessages() {
        LinkedHashMap<String, AbstractMessage<?>> retVal = new LinkedHashMap<String, AbstractMessage<?>>();
        retVal.put(Strings.getMessage("eventeditor.message"), getMessage());
        retVal.putAll(super.getAllMessages());
        return retVal;
    }

}
