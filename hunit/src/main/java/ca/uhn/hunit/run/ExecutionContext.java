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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.AbstractEvent;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.Log;

public class ExecutionContext {

	private Log myLog = new Log();

	private Map<TestImpl, TestFailureException> myTestFailures = new HashMap<TestImpl, TestFailureException>();
	private List<TestImpl> myTestSuccesses = new ArrayList<TestImpl>();

	private TestBatteryImpl myBattery;

	public ExecutionContext(TestBatteryImpl theBattery) {
		myBattery = theBattery;
	}

	public Log getLog() {
		return myLog;
	}

	public void setLog(Log theLog) {
		myLog = theLog;
	}

	public void addFailure(TestImpl theTest, TestFailureException theException) {
		getLog().info(theTest, "Failure: " + theException.getMessage());
	    myTestFailures.put(theTest, theException);
	}

	public void addSuccess(TestImpl theTest) {
        getLog().error(theTest, "Success!");
		myTestSuccesses.add(theTest);
	}

	public Map<TestImpl, TestFailureException> getTestFailures() {
		return myTestFailures;
	}

	public List<TestImpl> getTestSuccesses() {
		return myTestSuccesses;
	}

	public void execute() {
		execute((Set<String>)null);
	}

	public void execute(String... theTestNamesToExecute) {
		if (theTestNamesToExecute == null || theTestNamesToExecute.length == 0) {
			execute((Set<String>)null);
		} else {
		Set<String> testNames = new HashSet<String>(Arrays.asList(theTestNamesToExecute));
		execute(testNames);
		}
	}

	public void execute(Set<String> theTestNamesToExecute) {
		if (theTestNamesToExecute == null || theTestNamesToExecute.isEmpty()) {
			theTestNamesToExecute = myBattery.getTestNames();
		}
		
		getLog().info(myBattery, "About to execute battery");

		/* *****
		 * TODO: Use java.util.concurrent's executorservice instead of the
		 * busywaits ****
		 */

		List<TestImpl> tests = new ArrayList<TestImpl>();
		Map<String, TestBatteryExecutionThread> interface2thread = new HashMap<String, TestBatteryExecutionThread>();
		for (String nextTestNameId : myBattery.getTestNames2Tests().keySet()) {
			if (!theTestNamesToExecute.contains(nextTestNameId)) {
				continue;
			}

			TestImpl nextTest = myBattery.getTestNames2Tests().get(nextTestNameId);
			tests.add(nextTest);
			for (String nextInterfaceId : nextTest.getInterfacesUsed()) {
				if (interface2thread.containsKey(nextInterfaceId)) {
					continue;
				}

				AbstractInterface nextInterface;
				try {
					nextInterface = myBattery.getInterface(nextInterfaceId);
				} catch (ConfigurationException e) {
					throw new Error("Unknown interface ID[" + nextInterfaceId + "]. This should have already been caught, this is a bug");
				}
				TestBatteryExecutionThread thread = new TestBatteryExecutionThread(this, nextInterface);
				interface2thread.put(nextInterfaceId, thread);
				thread.start();

			}

		}

		// Wait until all threads are ready to start
		for (TestBatteryExecutionThread next : interface2thread.values()) {
			if (!next.isReady()) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// nothing
				}
			}
		}

		// Start executing
		for (TestImpl nextTest : tests) {
			for (Map.Entry<String, TestBatteryExecutionThread> nextEntry : interface2thread.entrySet()) {
				List<AbstractEvent> events = nextTest.getEventsForInterface(nextEntry.getKey());
				nextEntry.getValue().addEvents(events);
			}

			// Wait for all threads to catch up - This works but it's ugly
			boolean eventsPending;
			do {
				eventsPending = false;
				for (Entry<String, TestBatteryExecutionThread> next : interface2thread.entrySet()) {
				    TestBatteryExecutionThread nextThread = next.getValue();
				    if (nextThread.hasEventsPending()) {
						eventsPending = true;
					}
					if (myTestFailures.containsKey(nextTest)) {
					    break;
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						// nothing
					}
				}
			} while (eventsPending == true && !myTestFailures.containsKey(nextTest));

			// If we got out of the loop because of an error, cancel the other threads
            for (Map.Entry<String, TestBatteryExecutionThread> nextEntry : interface2thread.entrySet()) {
                nextEntry.getValue().cancelCurrentEvents();
            }
			
			// If we didn't fail, we succeeded :)
			if (!myTestFailures.containsKey(nextTest)) {
				addSuccess(nextTest);
			}

		}

		for (TestBatteryExecutionThread next : interface2thread.values()) {
			next.finish();
		}

	      // Wait until all threads are closed up
        for (TestBatteryExecutionThread next : interface2thread.values()) {
            if (next.isReady()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    // nothing
                }
            }
        }

		getLog().info(myBattery, "Finished executing battery");
	}

}
