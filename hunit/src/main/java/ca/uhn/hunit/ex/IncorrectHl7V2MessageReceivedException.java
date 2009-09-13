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
package ca.uhn.hunit.ex;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hunit.compare.hl7v2.FieldComparison;
import ca.uhn.hunit.compare.hl7v2.Hl7V2MessageCompare;
import ca.uhn.hunit.compare.hl7v2.SegmentComparison;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.test.TestImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IncorrectHl7V2MessageReceivedException extends TestFailureException
{

    private static final long serialVersionUID = -7116214031563429174L;

    private TestImpl myTest;
    private TestMessage myMessageReceived;
    private String myProblem;
    private TestMessage myMessageExpected;
    private Hl7V2MessageCompare myMessageCompare;


    public IncorrectHl7V2MessageReceivedException(TestImpl theExpect, TestMessage theMessageReceived,
            String theProblem) {
        this(theExpect, null, null, theMessageReceived, theProblem);
    }


    public IncorrectHl7V2MessageReceivedException(TestImpl theExpect, TestMessage theMessageExpected,
            TestMessage theMessageReceived, String theProblem) {
        this(theExpect, null, theMessageExpected, theMessageReceived, theProblem);
    }


    public IncorrectHl7V2MessageReceivedException(TestImpl theExpect, Throwable theCause,
            TestMessage theMessageReceived, String theProblem) {
        this(theExpect, theCause, null, theMessageReceived, theProblem);
    }


    public IncorrectHl7V2MessageReceivedException(TestImpl theExpect, Throwable theCause,
            TestMessage theMessageExpected, TestMessage theMessageReceived, String theProblem) {
        this(theExpect, theCause, theMessageExpected, theMessageReceived, theProblem, null);
    }


    public IncorrectHl7V2MessageReceivedException(TestImpl theTest, Throwable theCause, TestMessage theExpectMessage,
            TestMessage theActualMessage, String theProblem, Hl7V2MessageCompare theMessageCompare) {
        super(theCause);
        myTest = theTest;
        myMessageExpected = theExpectMessage;
        myMessageReceived = theActualMessage;
        myProblem = theProblem;
        myMessageCompare = theMessageCompare;
        if (myMessageExpected != null && myMessageReceived != null && myMessageCompare == null) {
            try {
                myMessageCompare = new Hl7V2MessageCompare();
                myMessageCompare.compare(theExpectMessage, theActualMessage);
            } catch (UnexpectedTestFailureException ex) {
                myMessageCompare = null;
                ex.printStackTrace();
                // TODO: what should we do with this?
            }
        }
    }


    public TestImpl getTest() {
        return myTest;
    }


    public TestMessage getMessageReceived() {
        return myMessageReceived;
    }


    public String getProblem() {
        return myProblem;
    }


    public TestMessage getMessageExpected() {
        return myMessageExpected;
    }


    @Override
    public String describeReason() {
        if (myMessageCompare != null) {
            return myMessageCompare.describeDifference();
        }

        StringBuilder retVal = new StringBuilder();
        retVal.append(myProblem).append("\r\n");
        retVal.append("Received: \r\n").append(Hl7V2MessageCompare.formatMsg(myMessageReceived)).append("\r\n");
        if (myMessageExpected != null) {
            retVal.append("Expected: \r\n").append(Hl7V2MessageCompare.formatMsg(myMessageExpected)).append("\r\n");
        }

        return retVal.toString();
    }



}
