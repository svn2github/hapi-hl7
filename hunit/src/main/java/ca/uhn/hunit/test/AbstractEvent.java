package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.Event;
import ca.uhn.hunit.xsd.Interface;

public abstract class AbstractEvent {

	private TestBatteryImpl myBattery;
	private String myInterfaceId;
	private TestImpl myTest;

	public AbstractEvent(TestBatteryImpl theBattery, TestImpl theTest, Event theConfig) {
		myInterfaceId = theConfig.getInterfaceId();
		myBattery = theBattery;		
		myTest = theTest;
	}
	
	public TestImpl getTest() {
		return myTest;
	}

	public abstract void execute(ExecutionContext theCtx) throws TestFailureException;

	public TestBatteryImpl getBattery() {
		return myBattery;
	}

	public String getInterfaceId() {
		return myInterfaceId;
	}

	public AbstractInterface getInterface() throws ConfigurationException {
		return myBattery.getInterface(myInterfaceId);
	}
	
}
