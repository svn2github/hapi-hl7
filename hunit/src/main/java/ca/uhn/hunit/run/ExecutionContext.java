package ca.uhn.hunit.run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		myTestFailures.put(theTest, theException);
	}

	public void addSuccess(TestImpl theTest) {
		myTestSuccesses.add(theTest);
	}

	public Map<TestImpl, TestFailureException> getTestFailures() {
		return myTestFailures;
	}

	public List<TestImpl> getTestSuccesses() {
		return myTestSuccesses;
	}

	public void execute() {
		execute(myBattery.getTestNames2Tests().keySet());
	}

	public void execute(String... theTestNamesToExecute) {
		Set<String> testNames = new HashSet<String>(Arrays.asList(theTestNamesToExecute));
		execute(testNames);
	}

	public void execute(Set<String> theTestNamesToExecute) {
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

				AbstractInterface nextInterface = myBattery.getInterface(nextInterfaceId);
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
				for (TestBatteryExecutionThread nextThread : interface2thread.values()) {
					if (nextThread.hasEventsPending()) {
						eventsPending = true;
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						// nothing
					}
				}
			} while (eventsPending == true);

			// If we didn't fail, we succeeded :)
			if (!myTestFailures.containsKey(nextTest)) {
				addSuccess(nextTest);
			}

		}

		for (TestBatteryExecutionThread next : interface2thread.values()) {
			next.finish();
		}
		
		getLog().info(myBattery, "Finished executing battery");
	}

}
