package ca.uhn.hunit.iface;

public class TestMessage {

	private String myRawMessage;
	private Object myParsedMessage;

	public TestMessage(String theRawMessage, Object theParsedMessage) {
		super();
		myRawMessage = theRawMessage;
		myParsedMessage = theParsedMessage;
	}

	public TestMessage(String theMessage) {
		myRawMessage = theMessage;
	}

	public Object getParsedMessage() {
		return myParsedMessage;
	}

	public String getRawMessage() {
		return myRawMessage;
	}

	public void setParsedMessage(Object theParsedMessage) {
		myParsedMessage = theParsedMessage;
	}

	public void setRawMessage(String theRawMessage) {
		myRawMessage = theRawMessage;
	}

}
