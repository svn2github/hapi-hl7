package ca.uhn.hunit.ex;

public class TestFailureException extends Exception {

	public TestFailureException() {
	}

	public TestFailureException(String theMessage) {
		super(theMessage);
	}

	public TestFailureException(Throwable theCause) {
		super(theCause);
	}

	public TestFailureException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

}
