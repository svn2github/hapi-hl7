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
package ca.uhn.hunit.msg;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.xsd.Hl7V2MessageDefinition;

import java.beans.PropertyVetoException;

import org.apache.commons.lang.StringUtils;

public class Hl7V2MessageImpl extends AbstractMessage<Message> {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private Message myParsedMessage;
    private String mySourceMessage;
    private String myText;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public Hl7V2MessageImpl(String theId) {
        super(theId);
    }

    public Hl7V2MessageImpl(Hl7V2MessageDefinition theConfig)
                     throws ConfigurationException {
        super(theConfig);

        try {
            setSourceMessage(theConfig.getText());
        } catch (PropertyVetoException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        }
    }

    /**
     * Constructor
     */
    public Hl7V2MessageImpl() {
        // nothing
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * Subclasses should make use of this method to export AbstractInterface properties into
     * the return value for {@link #exportConfigToXml()}
     */
    protected Hl7V2MessageDefinition exportConfig(Hl7V2MessageDefinition theConfig) {
        super.exportConfig(theConfig);
        theConfig.setText(mySourceMessage);

        return theConfig;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Hl7V2MessageDefinition exportConfigToXml() {
        return exportConfig(new Hl7V2MessageDefinition());
    }

    @Override
    public Class<Message> getMessageClass() {
        return Message.class;
    }

    @Override
    public String getSourceMessage() {
        return mySourceMessage;
    }

    @Override
    public TestMessage<Message> getTestMessage() {
        return new TestMessage<Message>(myText, myParsedMessage);
    }

    @Override
    public void setSourceMessage(String theMessage) throws PropertyVetoException {
        theMessage = StringUtils.defaultString(theMessage);
        
        String original = mySourceMessage;
        
        String sourceMessage = theMessage.trim();
        String text = sourceMessage.replaceAll("(\\r|\\n)+", "\r");

        PipeParser parser = new PipeParser();
        parser.setValidationContext(new ValidationContextImpl());

        Message parsedMessage;
        try {
            // Parse and re-encode to strip out any inconsistancies in the message (extra blank fields at the end of segments, etc)
            parsedMessage = parser.parse(text);
            text = parser.encode(parsedMessage);
        } catch (EncodingNotSupportedException e) {
            throw new PropertyVetoException(e.getMessage(), null);
        } catch (HL7Exception e) {
            throw new PropertyVetoException(e.getMessage(), null);
        }

        sourceMessage = text.replaceAll("\\r", "\r\n");

        myParsedMessage = parsedMessage;
        myText = text;
        mySourceMessage = sourceMessage;
        firePropertyChange(SOURCE_MESSAGE_PROPERTY, original, text);
    }
}
