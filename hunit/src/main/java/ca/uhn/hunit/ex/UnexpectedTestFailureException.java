package ca.uhn.hunit.ex;

public class UnexpectedTestFailureException extends TestFailureException {

	private static final long serialVersionUID = 5026570650177120006L;


	public UnexpectedTestFailureException() {
	}

	public UnexpectedTestFailureException(String theMessage) {
		super(theMessage);
	}

	public UnexpectedTestFailureException(Throwable theCause) {
		super(theCause);
	}

	public UnexpectedTestFailureException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}


	@Override
	public String describeReason() {
		return "Unexpected problem - " + getMessage();
	}

}
