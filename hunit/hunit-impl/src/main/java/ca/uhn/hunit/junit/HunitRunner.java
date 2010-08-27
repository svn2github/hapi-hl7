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

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.run.IExecutionListener;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import org.junit.runners.model.InitializationError;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

/**
 * JUNIT 4 test runner to allow hUnit tests to be run from within a JUNIT
 * test suite.
 *
 * This class is not yet complete.
 */
public class HunitRunner extends Runner {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private final ArrayList<String> myTestNames;
    private Class<?> myTestClass;
    private final Description myDescription;
    private final Map<String, Description> myTestName2Description = new HashMap<String, Description>();
    private final TestBatteryImpl myBattery;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public HunitRunner(Class<?> theTestClass) throws InitializationError {
        myTestClass = theTestClass;

        HunitBattery batteryAnnotation = myTestClass.getAnnotation(HunitBattery.class);

        try {
            Resource file = new DefaultResourceLoader().getResource(batteryAnnotation.file());

            if (! file.exists()) {
                throw new InitializationError("File doesn't exist: " + file.getDescription());
            }

            myBattery = new TestBatteryImpl(file);
        } catch (ConfigurationException ex) {
            throw new InitializationError(Collections.singletonList((Throwable) ex));
        } catch (JAXBException ex) {
            throw new InitializationError(Collections.singletonList((Throwable) ex));
        }

        myDescription = Description.createSuiteDescription(myBattery.getName());

        myTestNames = new ArrayList<String>();

        for (String nextTestName : myBattery.getTestNames()) {
            myTestNames.add(nextTestName);

            Description description = Description.createTestDescription(myTestClass, nextTestName);
            myDescription.addChild(description);
            myTestName2Description.put(nextTestName, description);
        }
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    @Override
    public Description getDescription() {
        return myDescription;
    }

    @Override
    public void run(final RunNotifier notifier) {
        ExecutionContext ctx = new ExecutionContext(myBattery);

        ctx.addListener(new IExecutionListener() {
                public void testFailed(TestImpl theTest, TestFailureException theException) {
                    Description description = myTestName2Description.get(theTest.getName());
                    Failure failure = new Failure(description, theException);
                    notifier.fireTestFailure(failure);
                }

                public void testPassed(TestImpl theTest) {
                    Description description = myTestName2Description.get(theTest.getName());
                    notifier.fireTestFinished(description);
                }

                public void testStarted(TestImpl theTest) {
                    Description description = myTestName2Description.get(theTest.getName());
                    notifier.fireTestStarted(description);
                }

                public void batteryStarted(TestBatteryImpl theBattery) {
                    // nothing
                }

                public void batteryFailed(TestBatteryImpl theBattery) {
                    // nothing
                }

                public void batteryPassed(TestBatteryImpl theBattery) {
                    // nothing
                }
            });

        //notifier.fireTestRunStarted(myDescription);
        ctx.execute(myTestNames);

        //notifier.fireTestRunFinished(result);
    }

    //private class My
}
