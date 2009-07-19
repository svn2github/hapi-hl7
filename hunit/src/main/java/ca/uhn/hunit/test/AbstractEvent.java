package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.Event;

public abstract class AbstractEvent {

	private TestBatteryImpl myBattery;
	private String myInterfaceId;

	public AbstractEvent(TestBatteryImpl theBattery, Event theConfig) {
		myInterfaceId = theConfig.getInterfaceId();
		myBattery = theBattery;		
	}
	
	public abstract void execute(ExecutionContext theCtx) throws InterfaceException, IncorrectMessageReceivedException;

	public TestBatteryImpl getBattery() {
		return myBattery;
	}

	public String getInterfaceId() {
		return myInterfaceId;
	}

	
}
