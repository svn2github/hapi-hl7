package ca.uhn.hunit.test;

import java.io.IOException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.DefaultApplication;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.HL7V2ExpectAbstract;

public abstract class AbstractHl7V2ExpectMessage extends AbstractExpectMessage {

	private Parser myParser;
	private Boolean myAutoAck;

	public AbstractHl7V2ExpectMessage(TestBatteryImpl theBattery, HL7V2ExpectAbstract theConfig) {
		super(theBattery, theConfig);
		
		if ("XML".equals(theConfig.getEncoding())) {
			myParser = new DefaultXMLParser();
		} else {
			myParser = new PipeParser();
		}
		myParser.setValidationContext(new ValidationContextImpl());

		myAutoAck = theConfig.isAutoAck();
		
		if (myAutoAck == null) {
			myAutoAck = true;
		}

	}

	@Override
	public void receiveMessage(ExecutionContext theCtx, String theMessage)
			throws TestFailureException {

		Message parsedMessage;
		try {
			parsedMessage = myParser.parse(theMessage);
		} catch (EncodingNotSupportedException e) {
			throw new IncorrectMessageReceivedException(this, theMessage, e.getMessage());
		} catch (HL7Exception e) {
			throw new IncorrectMessageReceivedException(this, theMessage, e.getMessage());
		}

		if (myAutoAck) {
			try {
				Message ack = DefaultApplication.makeACK((Segment) parsedMessage.get("MSH"));
				String reply = myParser.encode(ack);

				theCtx.getLog().info(getInterface(), "Sending HL7 v2 ACK (" + reply.length() + " bytes)");
				getInterface().sendMessage(theCtx, reply);
			} catch (EncodingNotSupportedException e) {
				throw new IncorrectMessageReceivedException(this, e, theMessage, "Problem generating ACK - " + e.getMessage());
			} catch (HL7Exception e) {
				throw new IncorrectMessageReceivedException(this, e, theMessage, "Problem generating ACK - " + e.getMessage());
			} catch (IOException e) {
				throw new IncorrectMessageReceivedException(this, e, theMessage, "Problem generating ACK - " + e.getMessage());
			}
			
		}
		
		validateMessage(theMessage, parsedMessage);
	}

	public abstract void validateMessage(String theRawMessage, Message theParsedMessage) throws IncorrectMessageReceivedException;
	
}
