package ca.uhn.hunit.ex;

import ca.uhn.hunit.iface.AbstractInterface;

public class InterfaceWontStartException extends InterfaceException {

	private static final long serialVersionUID = -1024559784773944592L;

	public InterfaceWontStartException(AbstractInterface theInterface,
			String theString, Throwable theCause) {
		super(theInterface, theString, theCause);
	}

	public InterfaceWontStartException(AbstractInterface theInterface, String theString) {
		super(theInterface, theString);
	}


	@Override
	public String describeReason() {
		return "Interface " + getInterface().getId() + " will not start - " + getMessage();
	}

}
