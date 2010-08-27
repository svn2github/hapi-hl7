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
package ca.uhn.hunit.event.send;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.XmlMessageImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.SendMessageAny;
import ca.uhn.hunit.xsd.XMLSendMessage;
import ca.uhn.hunit.xsd.XmlMessageDefinition;

import java.util.LinkedHashMap;
import org.w3c.dom.Document;

/**
 *
 * @author James
 */
public class XmlSendMessageImpl extends AbstractSendMessage<Document, XmlMessageImpl> {
    private XmlMessageImpl myMessage;

	//~ Constructors ---------------------------------------------------------------------------------------------------

    public XmlSendMessageImpl(TestImpl theTest, XMLSendMessage theConfig)
                       throws ConfigurationException {
        super(theTest, theConfig);
        
		XmlMessageDefinition configMessage = theConfig.getMessage();
		if (configMessage != null) {
			myMessage = new XmlMessageImpl(configMessage);
		} else {
			myMessage = (XmlMessageImpl) getMessage();
		}
        
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public XMLSendMessage exportConfig(XMLSendMessage theConfig) {
        if (theConfig == null) {
            theConfig = new XMLSendMessage();
        }

        super.exportConfig(theConfig);

        return theConfig;
    }

    @Override
    public XMLSendMessage exportConfigToXml() {
        XMLSendMessage retVal = exportConfig(new XMLSendMessage());

        return retVal;
    }

    /**
     * Overriding to provide a specific type requirement
     */
    @Override
    public SendMessageAny exportConfigToXmlAndEncapsulate() {
        SendMessageAny sendMessage = new SendMessageAny();
        sendMessage.setXml(exportConfigToXml());

        return sendMessage;
    }

    public Class<Document> getMessageClass() {
        return Document.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TestMessage<Document> massageMessage(TestMessage<Document> theInput) {
        return theInput;
    }

	@Override
	protected XmlMessageImpl provideMessage() {
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
