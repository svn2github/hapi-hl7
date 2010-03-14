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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.event.expect;

import org.w3c.dom.Document;

import ca.uhn.hunit.compare.xml.XmlMessageCompare;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.XmlMessageImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.ExpectMessageAny;
import ca.uhn.hunit.xsd.XMLExpectSpecificMessage;
import ca.uhn.hunit.xsd.XmlMessageDefinition;
import java.util.LinkedHashMap;

/**
 *
 * @author James
 */
public class XmlExpectSpecificMessageImpl extends AbstractXmlExpectMessage {
    //~ Constructors ---------------------------------------------------------------------------------------------------

    private XmlMessageImpl myMessage;

	public XmlExpectSpecificMessageImpl(TestImpl theTest, XMLExpectSpecificMessage theConfig)
                                 throws ConfigurationException {
        super(theTest, theConfig);
        
		XmlMessageDefinition configMessage = theConfig.getMessage();
		if (configMessage != null) {
			myMessage = new XmlMessageImpl(configMessage);
		} else {
			myMessage = super.provideLinkedMessage();
		}
		
        
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public XMLExpectSpecificMessage exportConfig(XMLExpectSpecificMessage theConfig) {
        if (theConfig == null) {
            theConfig = new XMLExpectSpecificMessage();
        }

        super.exportConfig(theConfig);

        return theConfig;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public XMLExpectSpecificMessage exportConfigToXml() {
        XMLExpectSpecificMessage retVal = exportConfig(new XMLExpectSpecificMessage());
        retVal.setMessage(myMessage.exportConfigToXml());
        return retVal;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ExpectMessageAny exportConfigToXmlAndEncapsulate() {
        ExpectMessageAny retVal = new ExpectMessageAny();
        retVal.setXmlSpecific(exportConfigToXml());

        return retVal;
    }

    @Override
    protected void validateMessage(TestMessage<Document> parsedMessage)
                            throws TestFailureException {
        XmlMessageCompare compare = new XmlMessageCompare();
        compare.compare(myMessage.getTestMessage().getParsedMessage(), parsedMessage.getParsedMessage());

        if (compare.isSame() == false) {
            throw new IncorrectMessageReceivedException(getTest(),
                                                        null,
                                                        myMessage.getTestMessage(),
                                                        parsedMessage,
                                                        "Inforrect message received",
                                                        compare);
        }
    }

    /**
     * Provides the message to be expected by this event
     */
    public XmlMessageImpl getMessage() {
        return myMessage;
    }



    /**
     * {@inheritDoc }
     */
    @Override
    public LinkedHashMap<String, AbstractMessage<?>> getAllMessages() {
        LinkedHashMap<String, AbstractMessage<?>> retVal = new LinkedHashMap<String, AbstractMessage<?>>();
        retVal.put(Strings.getMessage("eventeditor.message"), getMessage());
        return retVal;
    }



}
