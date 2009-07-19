package ca.uhn.hunit.iface;

import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.Interface;

public abstract class AbstractInterface {

	private Interface myConfig;
	private String myId;
	private Boolean myAutostart;

	public String getId() {
		return myId;
	}

	public AbstractInterface(Interface theConfig) {
		myConfig = theConfig;
		myId = theConfig.getId();
		myAutostart = theConfig.isAutostart();
		if (myAutostart == null) {
			myAutostart = false;
		}
	}

	public Interface getConfig() {
		return myConfig;
	}

	public abstract void start(ExecutionContext theCtx) throws InterfaceWontStartException;
	
	public abstract void stop(ExecutionContext theCtx);
	
	public abstract String receiveMessage(ExecutionContext theCtx) throws InterfaceException;
	
	public abstract void sendMessage(ExecutionContext theCtx, String theMessage) throws InterfaceException;

	public boolean isAutostart() {
		return myAutostart;
	}
}
