/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
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
package ca.uhn.hunit.compare.xml;

import ca.uhn.hunit.compare.ICompare;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.iface.TestMessage;
import java.io.IOException;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Implementation of ICompare which compares XML documents
 */
public class XmlMessageCompare implements ICompare<Document> {

    private DetailedDiff myDiff;

    public XmlMessageCompare() {
        XMLUnit.setIgnoreWhitespace(true);
    }

    /**
     * {@inheritDoc }
     */
    public void compare(TestMessage<Document> theExpectMessage, TestMessage<Document> theActualMessage) throws UnexpectedTestFailureException {

        try {
            Diff diff = new Diff(theExpectMessage.getRawMessage(), theActualMessage.getRawMessage());
            myDiff = new DetailedDiff(diff);
        } catch (SAXException ex) {
            throw new UnexpectedTestFailureException("Failure generating message diff", ex);
        } catch (IOException ex) {
            throw new UnexpectedTestFailureException("Failure generating message diff", ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    public boolean isSame() {
        return myDiff.similar();
    }

    /**
     * {@inheritDoc }
     */
    public String describeDifference() {
        StringBuffer buffer = new StringBuffer();
        myDiff.appendMessage(buffer);
        return buffer.toString();
    }




}
