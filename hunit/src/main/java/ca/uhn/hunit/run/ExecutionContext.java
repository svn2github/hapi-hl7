package ca.uhn.hunit.run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.Log;

public class ExecutionContext {

	private Log myLog = new Log();

	private Map<TestImpl, TestFailureException> myTestFailures = new HashMap<TestImpl, TestFailureException>();
	private Map<TestBatteryImpl, TestFailureException> myBatteryFailures = new HashMap<TestBatteryImpl, TestFailureException>();
	private List<TestImpl> myTestSuccesses = new ArrayList<TestImpl>();

	public Log getLog() {
		return myLog;
	}

	public void setLog(Log theLog) {
		myLog = theLog;
	}

	public void addFailure(TestImpl theTest, TestFailureException theException) {
		myTestFailures.put(theTest, theException);
	}

	public void addFailure(TestBatteryImpl theBattery, TestFailureException theException) {
		myBatteryFailures.put(theBattery, theException);
	}

	public void addSuccess(TestImpl theTest) {
		myTestSuccesses.add(theTest);		
	}

	public Map<TestImpl, TestFailureException> getTestFailures() {
		return myTestFailures;
	}

	public Map<TestBatteryImpl, TestFailureException> getBatteryFailures() {
		return myBatteryFailures;
	}

	public List<TestImpl> getTestSuccesses() {
		return myTestSuccesses;
	}

}
