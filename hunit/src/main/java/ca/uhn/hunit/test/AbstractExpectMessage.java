package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.ExpectMessage;

public abstract class AbstractExpectMessage extends AbstractEvent {

	private TestImpl myTest;

	public AbstractExpectMessage(TestBatteryImpl theBattery, TestImpl theTest, ExpectMessage theConfig) {
		super(theBattery, theConfig);

		myTest = theTest;
	}

	@Override
	public void execute(ExecutionContext theCtx) throws TestFailureException {

		AbstractInterface intf = getBattery().getInterface(getInterfaceId());
		intf.start(theCtx);

		TestMessage message = intf.receiveMessage(myTest, theCtx);
		receiveMessage(theCtx, message);

	}

	public TestImpl getTest() {
		return myTest;
	}

	public abstract void receiveMessage(ExecutionContext theCtx, TestMessage theMessage) throws TestFailureException;

}
