package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.HL7V2ExpectAbstract;

public abstract class AbstractHl7V2ExpectMessage extends AbstractExpectMessage {


	public AbstractHl7V2ExpectMessage(TestImpl theTest, TestBatteryImpl theBattery, HL7V2ExpectAbstract theConfig) throws ConfigurationException {
		super(theBattery, theTest, theConfig);
	}

	@Override
	public void receiveMessage(ExecutionContext theCtx, TestMessage theMessage)
			throws TestFailureException {
		validateMessage(theMessage);
	}

	public abstract void validateMessage(TestMessage theMessage) throws IncorrectMessageReceivedException;
	
}
