/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.factory;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * Loads two sets of messages from a file definition, and cross-references the pair.
 */
public class Hl7V2BatchMessagePairLoader {
    private final PipeParser myParser;

    public Hl7V2BatchMessagePairLoader(Reader theFile1, Reader theFile2) throws IOException, HL7Exception {

        myParser = new PipeParser();
        myParser.setValidationContext(new ValidationContextImpl());

        String string1 = IOUtils.toString(theFile1);
        String string2 = IOUtils.toString(theFile2);

        List<Message> messages1 = loadMessages(string1);
        List<Message> messages2 = loadMessages(string1);
    }

    private List<Message> loadMessages(String theString) throws HL7Exception {
        List<Message> retVal = new ArrayList<Message>();

        theString = theString.trim().replaceAll("(\\r|\\n)+", "\r");
        String[] split = theString.split("\\rMSH");
        for (String next : split) {
            next = next.trim();
            if (!next.startsWith("MSH")) {
                next = "MSH" + next;
            }

            Message nextMessage = myParser.parse(next);
            retVal.add(nextMessage);
        }

        return retVal;
    }

}
