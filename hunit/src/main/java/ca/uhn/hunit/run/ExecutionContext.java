package ca.uhn.hunit.run;

import java.util.HashMap;
import java.util.Map;

import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.Log;

public class ExecutionContext {

	private Log myLog = new Log();

	private Map<TestImpl, Exception> myTestFailures = new HashMap<TestImpl, Exception>();
	private Map<TestBatteryImpl, Exception> myBatteryFailures = new HashMap<TestBatteryImpl, Exception>();

	public Log getLog() {
		return myLog;
	}

	public void setLog(Log theLog) {
		myLog = theLog;
	}

	public void addFailure(TestImpl theTest, Exception theException) {
		myTestFailures.put(theTest, theException);
	}

	public void addFailure(TestBatteryImpl theBattery, Exception theException) {
		myBatteryFailures.put(theBattery, theException);
	}

}
