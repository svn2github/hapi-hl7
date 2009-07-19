package ca.uhn.hunit.ex;

public class ConfigurationException extends Exception {

	public ConfigurationException() {
	}

	public ConfigurationException(String theMessage) {
		super(theMessage);
	}

	public ConfigurationException(Throwable theCause) {
		super(theCause);
	}

	public ConfigurationException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

}
