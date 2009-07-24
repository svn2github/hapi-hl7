package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.xsd.Hl7V2ExpectSpecificMessage;
import ca.uhn.hunit.xsd.MessageDefinition;

public class Hl7V2ExpectSpecificMessageImpl extends AbstractHl7V2ExpectMessage {

	private String myMessageId;
	private TestImpl myTest;
	
	public Hl7V2ExpectSpecificMessageImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectSpecificMessage theConfig) {
		super(theTest, theBattery, theConfig);
		
		MessageDefinition message = (MessageDefinition)theConfig.getMessageId();
		myMessageId = message.getId();
		myTest = theTest;
	}

	@Override
	public void validateMessage(TestMessage theMessage)
			throws IncorrectMessageReceivedException {
		
		String expectMessage = getBattery().getMessage(myMessageId).getText();
		
		if (!expectMessage.trim().equals(theMessage.getRawMessage().trim())) {
			throw new IncorrectMessageReceivedException(myTest, expectMessage, theMessage.getRawMessage(), "Message did not match"); 
		}
		
	}

}
