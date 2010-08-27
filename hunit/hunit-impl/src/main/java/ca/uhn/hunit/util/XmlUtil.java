/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.util;

import ca.uhn.hunit.event.expect.AbstractXmlExpectMessage;
import ca.uhn.hunit.util.log.LogFactory;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author James
 */
public class XmlUtil {

    /**
     * Parse an XML string. NOT TUNED FOR EFFICIENCY, mostly for use in unit tests for now..
     */
    public static Document parseString(String theString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = parserFactory.newDocumentBuilder();
        parser.setErrorHandler(new AbstractXmlExpectMessage.MyErrorHandler(LogFactory.INSTANCE.getSystem(XmlUtil.class)));
        Document parsedMessage = parser.parse(new InputSource(new StringReader(theString)));

        return parsedMessage;
    }
}
