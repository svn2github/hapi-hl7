/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.factory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;

/**
 * Loads two sets of messages from a file definition, and cross-references the pair.
 */
public class Hl7V2BatchMessagePairLoader {
    private final PipeParser myParser;
    private List<Message> myMessages1;
    private List<Message> myMessages2;
    private List<String> myWarnings;
    private Map<Message, Message> myCorellatedMessages;

    /**
     * Constructor
     */
    public Hl7V2BatchMessagePairLoader()  {
        myWarnings = new ArrayList<String>();
        myParser = new PipeParser();
        myParser.setValidationContext(new ValidationContextImpl());
    }

    /**
     * Loads all messages from a set of files
     */
    public void loadMessagesFromFiles(Reader theInFile, Reader theOutFile) throws IOException, HL7Exception {
        String string1 = IOUtils.toString(theInFile);
        String string2 = IOUtils.toString(theOutFile);

        myMessages1 = loadMessages(string1);
        myMessages2 = loadMessages(string2);
    }

    
    public void corellateMessagesUsingControlId() throws HL7Exception {
        myCorellatedMessages = new HashMap<Message, Message>();
        
        Map<String, Message> mappedMessages1 = mapMessagesByControlId(myMessages1);
        Map<String, Message> mappedMessages2 = mapMessagesByControlId(myMessages2);

        for (Map.Entry<String, Message> nextEntry : mappedMessages1.entrySet()) {
            Message corellatedMessage = mappedMessages2.remove(nextEntry.getKey());
            myCorellatedMessages.put(nextEntry.getValue(), corellatedMessage);
        }
        
        for (Map.Entry<String, Message> nextEntry : mappedMessages2.entrySet()) {
            Segment msh = (Segment) nextEntry.getValue().get("MSH");
            myWarnings.add("Output message found with no corresponding input. MSH was: " + msh.encode());
        }        
    }
    
    private Map<String, Message> mapMessagesByControlId(List<Message> theMessages) throws HL7Exception {
        HashMap<String, Message> retVal = new HashMap<String, Message>();
        for (Message message : theMessages) {
            Segment msh = (Segment) message.get("MSH");
            String controlId = Terser.get(msh, 10, 0, 1, 1);
            
            if (StringUtils.isBlank(controlId)) {
                myWarnings.add("Found messsage with no control ID (MSH-10). MSH was: " + msh.encode());
                continue;
            }
            
            if (retVal.containsKey(controlId)) {
                myWarnings.add("Found more than one messsage with the same control ID (MSH-10). MSH was: " + msh.encode());
                continue;
            }

            retVal.put(controlId, message);
        }
        
        return retVal;
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
