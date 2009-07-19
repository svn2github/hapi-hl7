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

	public InterfaceWontReceiveException(AbstractInterface theInterface,
			Throwable theCause) {
		super(theInterface, theCause);
	}

}
