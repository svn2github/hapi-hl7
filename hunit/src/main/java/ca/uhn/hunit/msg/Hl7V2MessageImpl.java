package ca.uhn.hunit.msg;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.xsd.Hl7V2MessageDefinition;

public class Hl7V2MessageImpl extends AbstractMessage {

	private String myText;

	public Hl7V2MessageImpl(Hl7V2MessageDefinition theConfig) throws ConfigurationException {
		super(theConfig);

		myText = theConfig.getText().trim().replaceAll("(\\r|\\n)+", "\r");
		
		PipeParser parser = new PipeParser();
		parser.setValidationContext(new ValidationContextImpl());
		try {
		    // Parse and re-encode to strip out any inconsistancies in the message (extra blank fields at the end of segments, etc) 
            myText = parser.encode(parser.parse(myText));
        } catch (EncodingNotSupportedException e) {
            throw new ConfigurationException(e);
        } catch (HL7Exception e) {
            throw new ConfigurationException(e);
        }
	}

	@Override
	public String getText() {
		return myText;
	}
	
}
