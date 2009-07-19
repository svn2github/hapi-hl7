package ca.uhn.hunit.test;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.xsd.Hl7V2ExpectSpecificMessage;

public class Hl7V2ExpectSpecificMessageImpl extends AbstractHl7V2ExpectMessage {

	private AbstractMessage myMessage;
	
	public Hl7V2ExpectSpecificMessageImpl(TestBatteryImpl theBattery, Hl7V2ExpectSpecificMessage theConfig) {
		super(theBattery, theConfig);
		
		myMessage = theBattery.getMessage(theConfig.getSpecificMessage());
	}

	@Override
	public void validateMessage(String theRawMessage, Message theParsedMessage)
			throws IncorrectMessageReceivedException {
		
		String expectMessage = myMessage.getText();
		if (!expectMessage.equals(theRawMessage)) {
			throw new IncorrectMessageReceivedException(this, expectMessage, theRawMessage, "Message did not match"); 
		}
		
	}

}
