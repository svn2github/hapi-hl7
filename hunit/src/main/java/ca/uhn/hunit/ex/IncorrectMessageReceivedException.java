package ca.uhn.hunit.ex;

import ca.uhn.hunit.test.AbstractExpectMessage;


public class IncorrectMessageReceivedException extends TestFailureException {

	private AbstractExpectMessage myExpect;
	private String myMessageReceived;
	private String myProblem;
	private String myMessageExpected;

	public IncorrectMessageReceivedException(AbstractExpectMessage theExpect, String theMessageReceived, String theProblem) {
		myExpect = theExpect;
		myMessageReceived = theMessageReceived;
		myProblem = theProblem;
	}

	public IncorrectMessageReceivedException(AbstractExpectMessage theExpect, String theMessageExpected, String theMessageReceived, String theProblem) {
		myExpect = theExpect;
		myMessageExpected = theMessageExpected;
		myMessageReceived = theMessageReceived;
		myProblem = theProblem;
	}

	public AbstractExpectMessage getExpect() {
		return myExpect;
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

}
