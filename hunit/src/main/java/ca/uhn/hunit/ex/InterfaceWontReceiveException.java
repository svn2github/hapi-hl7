package ca.uhn.hunit.ex;

import ca.uhn.hunit.iface.AbstractInterface;

public class InterfaceWontReceiveException extends InterfaceException {

	public InterfaceWontReceiveException(AbstractInterface theInterface,
			String theString, Throwable theCause) {
		super(theInterface, theString, theCause);
	}

	public InterfaceWontReceiveException(AbstractInterface theInterface, String theString) {
		super(theInterface, theString);
	}

	@Override
	public String describeReason() {
		return "Interface " + getInterface().getId() + " did not receive expected message - " + getMessage();
	}

}
