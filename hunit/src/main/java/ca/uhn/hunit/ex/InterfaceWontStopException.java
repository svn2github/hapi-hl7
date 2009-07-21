package ca.uhn.hunit.ex;

import ca.uhn.hunit.iface.AbstractInterface;

public class InterfaceWontStopException extends InterfaceException {

	private static final long serialVersionUID = -8576106380412019367L;

	public InterfaceWontStopException(AbstractInterface theInterface,
			String theString, Throwable theCause) {
		super(theInterface, theString, theCause);
	}

	public InterfaceWontStopException(AbstractInterface theInterface, String theString) {
		super(theInterface, theString);
	}


	@Override
	public String describeReason() {
		return "Interface " + getInterface().getId() + " will not stop - " + getMessage();
	}

}
