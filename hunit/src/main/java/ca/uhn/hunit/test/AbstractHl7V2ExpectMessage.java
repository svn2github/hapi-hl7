package ca.uhn.hunit.test;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.xsd.ExpectMessage;

public abstract class AbstractHl7V2ExpectMessage extends AbstractExpectMessage {

	private GenericParser myParser;

	public AbstractHl7V2ExpectMessage(TestBatteryImpl theBattery, ExpectMessage theConfig) {
		super(theBattery, theConfig);
		
		myParser = new GenericParser();
		myParser.setValidationContext(new ValidationContextImpl());
		
	}

	@Override
	public void receiveMessage(String theMessage)
			throws IncorrectMessageReceivedException {

		Message parsedMessage;
		try {
			parsedMessage = myParser.parse(theMessage);
		} catch (EncodingNotSupportedException e) {
			throw new IncorrectMessageReceivedException(this, theMessage, e.getMessage());
		} catch (HL7Exception e) {
			throw new IncorrectMessageReceivedException(this, theMessage, e.getMessage());
		}

		validateMessage(theMessage, parsedMessage);
	}

	public abstract void validateMessage(String theRawMessage, Message theParsedMessage) throws IncorrectMessageReceivedException;
	
}
