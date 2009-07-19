package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.ExpectMessage;

public abstract class AbstractExpectMessage extends AbstractEvent {

	public AbstractExpectMessage(TestBatteryImpl theBattery, ExpectMessage theConfig) {
		super(theBattery, theConfig);
	}
	
	
	public abstract void receiveMessage(String theMessage) throws IncorrectMessageReceivedException;


	@Override
	public void execute(ExecutionContext theCtx) throws InterfaceException, IncorrectMessageReceivedException {
		
		AbstractInterface intf = getBattery().getInterface(getInterfaceId());
		intf.start(theCtx);
		
		String message = intf.receiveMessage(theCtx);
		receiveMessage(message);
		
	}
	
}
