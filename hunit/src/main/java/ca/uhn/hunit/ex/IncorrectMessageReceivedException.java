package ca.uhn.hunit.ex;

import ca.uhn.hunit.test.TestImpl;


public class IncorrectMessageReceivedException extends TestFailureException {

	private TestImpl myTest;
	private String myMessageReceived;
	private String myProblem;
	private String myMessageExpected;

	public IncorrectMessageReceivedException(TestImpl theExpect, String theMessageReceived, String theProblem) {
		myTest = theExpect;
		myMessageReceived = theMessageReceived;
		myProblem = theProblem;
	}

	public IncorrectMessageReceivedException(TestImpl theExpect, String theMessageExpected, String theMessageReceived, String theProblem) {
		myTest = theExpect;
		myMessageExpected = theMessageExpected;
		myMessageReceived = theMessageReceived;
		myProblem = theProblem;
	}

	public IncorrectMessageReceivedException(TestImpl theExpect, Throwable theCause, String theMessageReceived, String theProblem) {
		super(theCause);
		myTest = theExpect;
		myMessageReceived = theMessageReceived;
		myProblem = theProblem;
	}

	public IncorrectMessageReceivedException(TestImpl theExpect, Throwable theCause, String theMessageExpected, String theMessageReceived, String theProblem) {
		super(theCause);
		myTest = theExpect;
		myMessageExpected = theMessageExpected;
		myMessageReceived = theMessageReceived;
		myProblem = theProblem;
	}

	public TestImpl getTest() {
		return myTest;
	}

	public String getMessageReceived() {
		return myMessageReceived;
	}

	public String getProblem() {
		return myProblem;
	}

	public String getMessageExpected() {
		return myMessageExpected;
	}

	@Override
	public String describeReason() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(myProblem).append("\r\n");
		retVal.append("Received: \r\n").append(formatMsg(myMessageReceived)).append("\r\n");
		if (myMessageExpected != null) {
			retVal.append("Expected: \r\n").append(formatMsg(myMessageExpected)).append("\r\n");
		}
		return retVal.toString();
	}

	private String formatMsg(String theMessageExpected) {
		return "  " + theMessageExpected.replaceAll("(\\r|\\n)+", "\r\n  ");
	}

}
