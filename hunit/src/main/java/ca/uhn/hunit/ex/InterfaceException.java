package ca.uhn.hunit.ex;

import ca.uhn.hunit.iface.AbstractInterface;

public class InterfaceException extends Exception {


	private AbstractInterface myInterface;

	public InterfaceException(AbstractInterface theInterface, Throwable theCause) {
		super(theCause);
		myInterface = theInterface;
	}

	public InterfaceException(AbstractInterface theInterface, String theString) {
		super(theString);
		myInterface = theInterface;
	}
	
	public InterfaceException(AbstractInterface theInterface, String theString, Throwable theCause) {
		super(theString, theCause);
		myInterface = theInterface;
	}
	
}
