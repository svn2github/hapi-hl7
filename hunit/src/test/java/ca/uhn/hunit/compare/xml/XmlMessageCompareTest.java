/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.compare.xml;

import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.iface.TestMessage;

import ca.uhn.hunit.util.XmlUtil;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.Document;

/**
 *
 * @author James
 */
public class XmlMessageCompareTest {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private Log myLog = LogFactory.getLog(getClass());
    private XmlMessageCompare myCompare;

    //~ Methods --------------------------------------------------------------------------------------------------------

    @Before
    public void setUp() {
        myCompare = new XmlMessageCompare();
    }

    @Test
    public void testCompareIdentical() throws Exception {
        String expected = "<test><child1>content1</child1><child2>content2</child2></test>";
        String actual = "<test><child1>content1</child1><child2>content2</child2></test>";
        
        Document expectedDoc = XmlUtil.parseString(expected);
        Document actualDoc = XmlUtil.parseString(actual);
        myCompare.compare(expectedDoc, actualDoc);

        Assert.assertTrue(myCompare.describeDifference(),
                          myCompare.isSame());
    }

    @Test
    public void testCompareSameWithWhitespace() throws Exception {
        String expected = "<test><child1>content1</child1><child2>content2</child2></test>";
        String actual = "<test>\r\n  <child1>content1</child1>\r\n  <child2>content2</child2>\r\n</test>\r\n";

        Document expectedDoc = XmlUtil.parseString(expected);
        Document actualDoc = XmlUtil.parseString(actual);
        myCompare.compare(expectedDoc, actualDoc);

        Assert.assertTrue(myCompare.describeDifference(),
                          myCompare.isSame());
    }

    @Test
    public void testDifferentSameWithWhitespace() throws Exception {
        String expected = "<test><child1>content1</child1><child2>content2</child2></test>";
        String actual = "<test>\r\n  <child1>content2</child1>\r\n  <child2>content1</child2>\r\n</test>\r\n";

        Document expectedDoc = XmlUtil.parseString(expected);
        Document actualDoc = XmlUtil.parseString(actual);
        myCompare.compare(expectedDoc, actualDoc);

        Assert.assertFalse(myCompare.isSame());

        myLog.info(myCompare.describeDifference());
    }
}
