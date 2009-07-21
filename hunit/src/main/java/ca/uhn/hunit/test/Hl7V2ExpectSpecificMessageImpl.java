package ca.uhn.hunit.test;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.xsd.Hl7V2ExpectSpecificMessage;
import ca.uhn.hunit.xsd.MessageDefinition;

public class Hl7V2ExpectSpecificMessageImpl extends AbstractHl7V2ExpectMessage {

	private String myMessageId;
	
	public Hl7V2ExpectSpecificMessageImpl(TestBatteryImpl theBattery, Hl7V2ExpectSpecificMessage theConfig) {
		super(theBattery, theConfig);
		
		MessageDefinition message = (MessageDefinition)theConfig.getMessageId();
		myMessageId = message.getId();
		
	}

	@Override
	public void validateMessage(String theRawMessage, Message theParsedMessage)
			throws IncorrectMessageReceivedException {
		
		String expectMessage = getBattery().getMessage(myMessageId).getText();
		if (!expectMessage.equals(theRawMessage)) {
			throw new IncorrectMessageReceivedException(this, expectMessage, theRawMessage, "Message did not match"); 
		}
		
	}

}
