package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.ExpectMessage;

public abstract class AbstractExpectMessage extends AbstractEvent {

	private TestImpl myTest;
	private AbstractInterface myInterface;

	public AbstractExpectMessage(TestBatteryImpl theBattery, TestImpl theTest, ExpectMessage theConfig) throws ConfigurationException {
		super(theBattery, theTest, theConfig);

		myTest = theTest;
		myInterface = getBattery().getInterface(getInterfaceId());
	}

	@Override
	public void execute(ExecutionContext theCtx) throws TestFailureException {

		myInterface.start(theCtx);

		TestMessage message = myInterface.receiveMessage(myTest, theCtx);
		receiveMessage(theCtx, message);

	}

	public TestImpl getTest() {
		return myTest;
	}

	public abstract void receiveMessage(ExecutionContext theCtx, TestMessage theMessage) throws TestFailureException;

}
