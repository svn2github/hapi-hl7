package ca.uhn.hunit.msg;

import ca.uhn.hunit.xsd.Hl7V2MessageDefinition;

public class Hl7V2MessageImpl extends AbstractMessage {


	private String myText;

	public Hl7V2MessageImpl(Hl7V2MessageDefinition theConfig) {
		super(theConfig);

		myText = theConfig.getText().trim().replaceAll("(\\r|\\n)+", "\r");
	}

	@Override
	public String getText() {
		return myText;
	}
	
}
