package ca.uhn.hunit.msg;

import ca.uhn.hunit.xsd.MessageDefinition;

public abstract class AbstractMessage {

	protected String myId;

	public AbstractMessage(MessageDefinition theConfig) {
		myId = theConfig.getId();
	}

	public abstract String getText();

	public String getId() {
		return myId;
	}

}