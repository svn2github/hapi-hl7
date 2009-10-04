/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
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

import ca.uhn.hunit.test.*;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hunit.compare.hl7v2.Hl7V2MessageCompare;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.xsd.Hl7V2ExpectSpecificMessage;

public class Hl7V2ExpectSpecificMessageImpl extends AbstractHl7V2ExpectMessage {

	private String myMessageId;
	private Hl7V2MessageImpl myMessageProvider;
	
	public Hl7V2ExpectSpecificMessageImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectSpecificMessage theConfig) throws ConfigurationException {
		super(theTest, theBattery, theConfig);
		
		myMessageId = theConfig.getMessageId();
		AbstractMessage messageProvider = getBattery().getMessage(myMessageId);
		if (!(messageProvider instanceof Hl7V2MessageImpl)) {
			throw new ConfigurationException("Message with ID[" + myMessageId + "] is not an HL7 v2 message type so it can not be used with this expect");
		}
		myMessageProvider = (Hl7V2MessageImpl) messageProvider;
	}

	@Override
	public void validateMessage(TestMessage<Message> theMessage)
			throws TestFailureException {
		
		TestMessage<Message> expectMessage = myMessageProvider.getTestMessage();
		TestMessage<Message> actualMessage = theMessage;
		
		Hl7V2MessageCompare messageCompare = new Hl7V2MessageCompare();
        messageCompare.compare(expectMessage, actualMessage);
		if (!messageCompare.isSame()) {
			throw new IncorrectMessageReceivedException(getTest(), null, expectMessage, actualMessage, "Messages did not match", messageCompare);
		}
		
	}

}
