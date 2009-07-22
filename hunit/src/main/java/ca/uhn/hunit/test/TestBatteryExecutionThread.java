package ca.uhn.hunit.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.run.ExecutionContext;

public class TestBatteryExecutionThread extends Thread {

	private ExecutionContext myCtx;
	private TestImpl myTest;
	private String myInterfaceId;
	private boolean myStopped;
	private AbstractInterface myInterface;
	private boolean myFailed;
	private TestBatteryImpl myBattery;
	private List<AbstractEvent> myEvents = new LinkedList<AbstractEvent>();
	private AbstractEvent myCurrentEvent;

	public TestBatteryExecutionThread(ExecutionContext theCtx, TestBatteryImpl theBattery, TestImpl theTest, String theInterfaceId) {
		super(theInterfaceId);
		myTest = theTest;
		myInterfaceId = theInterfaceId;
		myBattery = theBattery;
		myInterface = theBattery.getInterface(theInterfaceId);
		myCtx = theCtx;
		myStopped = false;
	}

	@Override
	public void run() {

		try {
			if (myInterface.isAutostart()) {
				myInterface.start(myCtx);
			}
		} catch (InterfaceWontStartException e) {
			myCtx.addFailure(myBattery, e);
			myFailed = true;
		}

		while (!myStopped) {

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// ignore
			}

			synchronized (myEvents) {
				if (myEvents.isEmpty()) {
					continue;
				}

				myCurrentEvent = myEvents.get(0);
			}

			try {
				myCurrentEvent.execute(myCtx);
			} catch (TestFailureException e) {
				myCtx.addFailure(myTest, e);
				myFailed = true;
				return;
			}

			synchronized (myEvents) {
				if (!myEvents.isEmpty()) {
					myEvents.remove(0);
				}
			}

			try {
				myInterface.stop(myCtx);
			} catch (InterfaceWontStopException e) {
				myCtx.addFailure(myBattery, e);
			}
			
			myStopped = true;
		}

	}

	public void addEvents(List<AbstractEvent> theEvents) {
		synchronized (myEvents) {
			myEvents.addAll(theEvents);
		}
	}

	public boolean hasEventsPending() {
		synchronized (myEvents) {
			return myEvents.isEmpty() == false;
		}
	}

	public boolean isFailed() {
		return myFailed;
	}
	
}
