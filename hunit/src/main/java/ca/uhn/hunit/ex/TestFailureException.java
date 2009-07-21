package ca.uhn.hunit.ex;

public abstract class TestFailureException extends Exception {

	private static final long serialVersionUID = 2609790450076964563L;

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

	public abstract String describeReason();

}
