/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * The Initial Developer of the Original Code is University Health Network. Copyright (C)
 * 2001.  All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the  "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */
package ca.uhn.hunit.run;

import ca.uhn.hunit.event.AbstractEvent;
import java.util.LinkedList;
import java.util.List;

import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.event.expect.AbstractExpect;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;

public class TestBatteryExecutionThread extends Thread {

	private ExecutionContext myCtx;
	private boolean myStopped;
	private AbstractInterface myInterface;
	private TestFailureException myFailed;
	private final List<AbstractEvent> myEvents = new LinkedList<AbstractEvent>();
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
				myCtx.getLog().get(myInterface).info("Interface is marked as \"autostart\". Going to start it.");
                myInterface.start(myCtx);
			}
		} catch (InterfaceWontStartException e) {
			myFailed = e;
		}

		myCtx.getLog().get(myInterface).info("Ready to begin execution");
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
			} catch (InterfaceException e) {
				myFailed = e;
				myCtx.addFailure(myCurrentEvent.getTest(), e);
				break;
			} catch (TestFailureException e) {
				myCtx.addFailure(myCurrentEvent.getTest(), e);
				synchronized (myEvents) {
					myEvents.clear();
				}
			} catch (Exception e) {
                myCtx.getLog().get(myCtx.getBattery()).error("Unexpected failure during test execution: " + e.getMessage(), e);
                myFailed = new UnexpectedTestFailureException(e);
				myCtx.addFailure(myCurrentEvent.getTest(), myFailed);
                break;
            }

			synchronized (myEvents) {
				if (!myEvents.isEmpty()) {
					myEvents.remove(myCurrentEvent);
				}
			}

		}

        stopInterface();

		myReady = false;

	}

	public void addEvent(AbstractEvent theEvent) {
		if (myFailed != null) {
				myCtx.addFailure(theEvent.getTest(), myFailed);
		} else {
			synchronized (myEvents) {
				myEvents.add(theEvent);
			}
		}
	}

	public boolean hasEventsPending() {
		synchronized (myEvents) {
		    if (myFailed != null) {
		        return false;
		    }
		    if (myEvents.isEmpty()) {
		        return false;
		    }
		    
		    boolean retVal = false;
		    for (AbstractEvent next : myEvents) {
		        if (next instanceof AbstractExpect) {
		            if (((AbstractExpect)next).isWaitForCompletion()) {
		                retVal = true;
		            }
		        } else {
		            retVal = true;
		        }
		    }
			return retVal;
		}
	}

	public void finish() {
		myStopped = true;
        stopInterface();
	}

	public void cancelCurrentEvents() {
		synchronized (myEvents) {
			myEvents.clear();
		}
	}

    private synchronized void stopInterface() {
        try {
            if (myInterface.isStarted()) {
                myInterface.stop(myCtx);
            }
		} catch (InterfaceWontStopException e) {
			myFailed = e;
			myCtx.getLog().get(myInterface).error("Can't stop interface: " + e.describeReason());
		}
    }

}
