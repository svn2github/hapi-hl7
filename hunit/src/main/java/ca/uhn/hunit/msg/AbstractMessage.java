package ca.uhn.hunit.msg;

import ca.uhn.hunit.xsd.MessageDefinition;

public class AbstractMessage {

	protected String myId;
	private String myText;

	public AbstractMessage(MessageDefinition theConfig) {
		myId = theConfig.getId();
		myText = theConfig.getText();
	}

	public String getText() {
		return myText;
	}

	public String getId() {
		return myId;
	}

}