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
package ca.uhn.hunit.junit;

import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.run.IExecutionListener;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;

import junit.framework.AssertionFailedError;

/**
 * Execution listener which generates JUnit 3 assertion failures when a test fails
 */
public class Junit3FailureListener implements IExecutionListener {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public void batteryFailed(TestBatteryImpl theBattery) {
        // nothing
    }

    public void batteryPassed(TestBatteryImpl theBattery) {
        // nothing
    }

    public void batteryStarted(TestBatteryImpl theBattery) {
        // nothing
    }

    public void testFailed(TestImpl theTest, TestFailureException theException) {
        throw new AssertionFailedError(theTest.getName() + " Failure! - " + theException.describeReason());
    }

    public void testPassed(TestImpl theTest) {
        // nothing
    }

    public void testStarted(TestImpl theTest) {
        // nothing
    }
}
