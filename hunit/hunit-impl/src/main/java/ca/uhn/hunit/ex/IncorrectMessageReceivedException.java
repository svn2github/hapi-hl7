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
package ca.uhn.hunit.ex;

import ca.uhn.hunit.compare.ICompare;
import ca.uhn.hunit.compare.hl7v2.Hl7V2MessageCompare;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.StringUtil;

/**
 * Test Failure exception for the case where a message was received, but
 * it was incorrect in some way
 */
public class IncorrectMessageReceivedException extends TestFailureException {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final long serialVersionUID = -7116214031563429174L;

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private ICompare<?> myMessageCompare;
    private String myProblem;
    private TestImpl myTest;
    private TestMessage<?> myMessageExpected;
    private TestMessage<?> myMessageReceived;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public IncorrectMessageReceivedException(TestImpl theExpect, TestMessage<?> theMessageReceived, String theProblem) {
        this(theExpect, null, null, theMessageReceived, theProblem);
    }

    public IncorrectMessageReceivedException(TestImpl theExpect, TestMessage<?> theMessageExpected,
                                             TestMessage<?> theMessageReceived, String theProblem) {
        this(theExpect, null, theMessageExpected, theMessageReceived, theProblem);
    }

    public IncorrectMessageReceivedException(TestImpl theExpect, Throwable theCause, TestMessage<?> theMessageReceived,
                                             String theProblem) {
        this(theExpect, theCause, null, theMessageReceived, theProblem);
    }

    public IncorrectMessageReceivedException(TestImpl theExpect, Throwable theCause, TestMessage<?> theMessageExpected,
                                             TestMessage<?> theMessageReceived, String theProblem) {
        this(theExpect, theCause, theMessageExpected, theMessageReceived, theProblem, null);
    }

    public IncorrectMessageReceivedException(TestImpl theTest, Throwable theCause, TestMessage<?> theExpectMessage,
                                             TestMessage<?> theActualMessage, String theProblem,
                                             ICompare<?> theMessageCompare) {
        super(Strings.getMessage("execution.failure.title.ca.uhn.hunit.ex.IncorrectMessageReceivedException"), theCause);
        myTest = theTest;
        myMessageExpected = theExpectMessage;
        myMessageReceived = theActualMessage;
        myProblem = theProblem;
        myMessageCompare = theMessageCompare;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    @Override
    public String describeReason() {
        StringBuilder retVal = new StringBuilder();
        retVal.append(myProblem).append("\r\n");
        retVal.append("Received: \r\n").append(Hl7V2MessageCompare.formatMsg(myMessageReceived)).append("\r\n");

        if (myMessageExpected != null) {
            retVal.append("Expected: \r\n").append(Hl7V2MessageCompare.formatMsg(myMessageExpected)).append("\r\n");
        }

        if (myMessageCompare != null) {
            retVal.append("Difference: \r\n");

            String describeDifference = myMessageCompare.describeDifference();
            describeDifference = StringUtil.prependEachLine(describeDifference, "  ");
            retVal.append(describeDifference);
        }

        return retVal.toString();
    }

    public TestMessage<?> getMessageExpected() {
        return myMessageExpected;
    }

    public TestMessage<?> getMessageReceived() {
        return myMessageReceived;
    }

    public String getProblem() {
        return myProblem;
    }

    public TestImpl getTest() {
        return myTest;
    }
}
