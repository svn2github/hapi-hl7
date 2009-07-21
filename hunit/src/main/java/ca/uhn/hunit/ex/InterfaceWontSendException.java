package ca.uhn.hunit.ex;

import ca.uhn.hunit.iface.AbstractInterface;

public class InterfaceWontSendException extends InterfaceException {

	public InterfaceWontSendException(AbstractInterface theInterface,
			String theString, Throwable theCause) {
		super(theInterface, theString, theCause);
	}

	public InterfaceWontSendException(AbstractInterface theInterface, String theString) {
		super(theInterface, theString);
	}

	@Override
	public String describeReason() {
		return "Interface " + getInterface().getId() + " could not send message - " + getMessage();
	}

}
