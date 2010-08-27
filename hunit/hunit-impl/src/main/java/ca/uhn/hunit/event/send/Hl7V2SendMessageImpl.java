/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * The Initial Developer of the Original Code is University Health Network. Copyright (C)
 * 2001.  All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the  "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */
package ca.uhn.hunit.event.send;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.test.*;
import ca.uhn.hunit.xsd.Event;
import ca.uhn.hunit.xsd.Hl7V2MessageDefinition;
import ca.uhn.hunit.xsd.Hl7V2SendMessage;
import ca.uhn.hunit.xsd.SendMessageAny;
import java.util.LinkedHashMap;

public class Hl7V2SendMessageImpl extends AbstractSendMessage<Message, Hl7V2MessageImpl> {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private Parser myParser;
    private String myEncoding;
	private Hl7V2MessageImpl myMessage;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public Hl7V2SendMessageImpl(TestImpl theTest, Hl7V2SendMessage theConfig)
                         throws ConfigurationException {
        super(theTest, theConfig);

        myEncoding = theConfig.getEncoding();

        if ("XML".equals(myEncoding)) {
            myParser = new DefaultXMLParser();
        } else {
            myParser = new PipeParser();
            myEncoding = "ER7";
        }

		Hl7V2MessageDefinition configMessage = theConfig.getMessage();
		if (configMessage != null) {
			myMessage = new Hl7V2MessageImpl(configMessage);
		} else {
			myMessage = (Hl7V2MessageImpl) getMessage();
		}
        
        myParser.setValidationContext(new ValidationContextImpl());
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public Hl7V2SendMessage exportConfig(Hl7V2SendMessage theConfig) {
        if (theConfig == null) {
            theConfig = new Hl7V2SendMessage();
        }

        super.exportConfig(theConfig);
        theConfig.setEncoding(myEncoding);
        theConfig.setMessage(myMessage.exportConfigToXml());
        return theConfig;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Hl7V2SendMessage exportConfigToXml() {
        Hl7V2SendMessage retVal = exportConfig(new Hl7V2SendMessage());
        return retVal;
    }

    /**
     * Overriding to provide a specific type requirement
     */
    @Override
    public SendMessageAny exportConfigToXmlAndEncapsulate() {
        SendMessageAny sendMessage = new SendMessageAny();
        sendMessage.setHl7V2(exportConfigToXml());

        return sendMessage;
    }

    public Class<Message> getMessageClass() {
        return Message.class;
    }

    @Override
    public TestMessage<Message> massageMessage(TestMessage<Message> theInput)
                                        throws UnexpectedTestFailureException {
        try {
            final Message parsedMessage = theInput.getParsedMessage();
            TestMessage<Message> retVal = new TestMessage<Message>(myParser.encode(parsedMessage),
                                                                   parsedMessage);

            return retVal;
        } catch (HL7Exception ex) {
            throw new UnexpectedTestFailureException("Unable to encode message", ex);
        }
    }

	@Override
	protected AbstractMessage<Message> provideMessage() {
		return myMessage;
	}



    /**
     * {@inheritDoc }
     */
    @Override
    public LinkedHashMap<String, AbstractMessage<?>> getAllMessages() {
        LinkedHashMap<String, AbstractMessage<?>> retVal = new LinkedHashMap<String, AbstractMessage<?>>();
        retVal.put(Strings.getMessage("eventeditor.message"), provideMessage());
        return retVal;
    }


}
