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

package ca.uhn.hunit.junit;

import java.util.List;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

/**
 * JUNIT 4 test runner to allow hUnit tests to be run from within a JUNIT
 * test suite.
 *
 * This class is not yet complete.
 */
public class HunitRunner extends ParentRunner<Runner> {

    private Class<?> myTestClass;

    public HunitRunner(Class<?> theTestClass) throws InitializationError {
        super(theTestClass);

        myTestClass = theTestClass;
    }

    @Override
    public Description getDescription() {
        Description retVal = Description.createTestDescription(null, null);
        return retVal;
    }

    @Override
    public void run(RunNotifier arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List getChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Description describeChild(Runner child) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void runChild(Runner child, RunNotifier notifier) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
