package ca.uhn.hunit.run;

import java.util.LinkedList;
import java.util.List;

import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.AbstractEvent;

public class TestBatteryExecutionThread extends Thread {

	private ExecutionContext myCtx;
	private boolean myStopped;
	private AbstractInterface myInterface;
	private TestFailureException myFailed;
	private List<AbstractEvent> myEvents = new LinkedList<AbstractEvent>();
	private AbstractEvent myCurrentEvent;
    private boolean myReady = false;

	
	public TestBatteryExecutionThread(ExecutionContext theExecutionContext, AbstractInterface theInterface) {
		super(theInterface.getId());

		myInterface = theInterface;
		myCtx = theExecutionContext;
	}


	/**
     * @return Returns the waiting.
     */
    public boolean isReady() {
        return myReady;
    }

    @Override
	public void run() {

		try {
			if (myInterface.isAutostart() && !myInterface.isStarted()) {
				myInterface.start(myCtx);
			}
		} catch (InterfaceWontStartException e) {
			myFailed = e;
		}

		myReady = true;
		
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

			if (myFailed != null) {
				myCtx.addFailure(myCurrentEvent.getTest(), myFailed);
				return;
			}
			
			try {
				myCurrentEvent.execute(myCtx);
			} catch (TestFailureException e) {
				myFailed = e;
				myCtx.addFailure(myCurrentEvent.getTest(), e);
				return;
			}

			synchronized (myEvents) {
				if (!myEvents.isEmpty()) {
					myEvents.remove(myCurrentEvent);
				}
			}

		}

        try {
            myInterface.stop(myCtx);
        } catch (InterfaceWontStopException e) {
        	myFailed = e;
            myCtx.getLog().error(myInterface, "Can't stop interface: " + e.describeReason());
        }
        
	}

	public void addEvents(List<AbstractEvent> theEvents) {
	    synchronized (myEvents) {
			myEvents.addAll(theEvents);
		}
	}

	public boolean hasEventsPending() {
		synchronized (myEvents) {
			return (myEvents.isEmpty() == false) && (myFailed == null);
		}
	}

	public void finish() {
	    myStopped = true;
	}
	
	public void cancelCurrentEvents() {
		synchronized (myEvents) {
			myEvents.clear();
		}
	}
	
}
