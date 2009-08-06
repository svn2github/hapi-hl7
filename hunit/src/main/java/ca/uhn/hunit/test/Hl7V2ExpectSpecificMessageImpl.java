package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.xsd.Hl7V2ExpectSpecificMessage;

public class Hl7V2ExpectSpecificMessageImpl extends AbstractHl7V2ExpectMessage {

	private String myMessageId;
	private TestImpl myTest;
	private AbstractMessage myMessageProvider;
	
	public Hl7V2ExpectSpecificMessageImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectSpecificMessage theConfig) throws ConfigurationException {
		super(theTest, theBattery, theConfig);
		
		myMessageId = theConfig.getMessageId();
		myMessageProvider = getBattery().getMessage(myMessageId);
		myTest = theTest;
	}

	@Override
	public void validateMessage(TestMessage theMessage)
			throws IncorrectMessageReceivedException {
		
		String expectMessage = myMessageProvider.getText();
		
		if (!expectMessage.trim().equals(theMessage.getRawMessage().trim())) {
			throw new IncorrectMessageReceivedException(myTest, expectMessage, theMessage.getRawMessage(), "Message did not match"); 
		}
		
	}

}
