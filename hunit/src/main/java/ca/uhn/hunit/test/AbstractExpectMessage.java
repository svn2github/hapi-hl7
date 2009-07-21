package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.ExpectMessage;

public abstract class AbstractExpectMessage extends AbstractEvent {

	public AbstractExpectMessage(TestBatteryImpl theBattery, ExpectMessage theConfig) {
		super(theBattery, theConfig);
	}
	
	
	public abstract void receiveMessage(ExecutionContext theCtx, String theMessage) throws TestFailureException;


	@Override
	public void execute(ExecutionContext theCtx) throws TestFailureException {
		
		AbstractInterface intf = getBattery().getInterface(getInterfaceId());
		intf.start(theCtx);
		
		String message = intf.receiveMessage(theCtx);
		receiveMessage(theCtx, message);
		
	}
	
}
