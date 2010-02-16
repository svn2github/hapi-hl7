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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.msg.XmlMessageImpl;
import ca.uhn.hunit.run.IExecutionContext;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.log.LogFactory;
import ca.uhn.hunit.xsd.Event;
import ca.uhn.hunit.xsd.XMLExpectMessage;
import org.apache.commons.logging.Log;

/**
 * Abstract test event to expect an XML message
 */
public abstract class AbstractXmlExpectMessage extends AbstractExpectMessage<XmlMessageImpl> {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private Boolean myValidateMessage;
    private final DocumentBuilderFactory myParserFactory;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public AbstractXmlExpectMessage(TestImpl theTest, XMLExpectMessage theConfig)
                             throws ConfigurationException {
        super(theTest, theConfig);

        myParserFactory = DocumentBuilderFactory.newInstance();
        myValidateMessage = theConfig.isValidateMessageUsingDTD();

        if (myValidateMessage == null) {
            myValidateMessage = false;
        }

        myParserFactory.setValidating(myValidateMessage);
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public Event exportConfig(XMLExpectMessage theConfig) {
        super.exportConfig(theConfig);
        theConfig.setValidateMessageUsingDTD(myValidateMessage);

        return theConfig;
    }

    @Override
    public void receiveMessage(IExecutionContext theCtx, TestMessage<?> theMessage)
                        throws TestFailureException {
        Document parsedMessage = (Document) theMessage.getParsedMessage();

        if (parsedMessage == null) {
            final String rawMessage = theMessage.getRawMessage();

            try {
                DocumentBuilder parser = myParserFactory.newDocumentBuilder();
                parser.setErrorHandler(new MyErrorHandler(LogFactory.INSTANCE.get(getTest())));
                parsedMessage = parser.parse(new InputSource(new StringReader(rawMessage)));
            } catch (ParserConfigurationException ex) {
                throw new UnexpectedTestFailureException("Unable to set up XML parser", ex);
            } catch (SAXException ex) {
                throw new IncorrectMessageReceivedException(getTest(),
                                                            theMessage,
                                                            "Unable to parse incoming message: " + ex.getMessage());
            } catch (IOException ex) {
                throw new UnexpectedTestFailureException(ex);
            }

            TestMessage<Document> testMessage = new TestMessage<Document>(rawMessage, parsedMessage);
            validateMessage(testMessage);
        }
    }

    /**
     * Subclasses must override this method to validate the message received
     */
    protected abstract void validateMessage(TestMessage<Document> parsedMessage)
                                     throws TestFailureException;

    //~ Inner Classes --------------------------------------------------------------------------------------------------

    public static class MyErrorHandler implements ErrorHandler {
        private final Log myLog;

        public MyErrorHandler(Log theLog) {
            myLog = theLog;
        }

        @Override
        public void error(SAXParseException theArg0) throws SAXException {
            throw new SAXException(theArg0);
        }

        @Override
        public void fatalError(SAXParseException theArg0)
                        throws SAXException {
            throw new SAXException(theArg0);
        }

        @Override
        public void warning(SAXParseException theArg0)
                     throws SAXException {
        	myLog.warn("XML Parsing Warning: " + theArg0.getMessage());
        }
    }
}
