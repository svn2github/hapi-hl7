package ca.uhn.hunit.ex;

import ca.uhn.hunit.iface.AbstractInterface;

public class InterfaceWontStartException extends InterfaceException {

	public InterfaceWontStartException(AbstractInterface theInterface,
			String theString, Throwable theCause) {
		super(theInterface, theString, theCause);
	}

	public InterfaceWontStartException(AbstractInterface theInterface, String theString) {
		super(theInterface, theString);
	}

	public InterfaceWontStartException(AbstractInterface theInterface,
			Throwable theCause) {
		super(theInterface, theCause);
	}

}
