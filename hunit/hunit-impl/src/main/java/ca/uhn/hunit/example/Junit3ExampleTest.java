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
package ca.uhn.hunit.example;

import ca.uhn.hunit.junit.*;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import org.springframework.util.ResourceUtils;

import java.io.File;

/**
 * This class gives an example of how to create a JUnit 3 test
 */
public class Junit3ExampleTest extends TestCase {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private ExecutionContext myExecutionContext;

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * In the setup method, prepare the test
     */
    public void setUp() throws Exception {
        /*
         * In the setup method, load the XML test file you are using. Here we are
         * using a Spring Framework utility class to load a File from the classpath,
         * but any other way of getting a file reference works too.
         */
        Resource file = new ClassPathResource("/ca/uhn/hunit/junit/unit_tests_many_passing.xml");
        TestBatteryImpl battery = new TestBatteryImpl(file);

        myExecutionContext = new ExecutionContext(battery);

        // This listener generates JUnit 3 assert failures if a test fails
        myExecutionContext.addListener(new Junit3FailureListener());
    }

    /**
     * Then, in the test methods, just execute the specific test
     */
    public void testSomething1() throws Exception {
        myExecutionContext.execute("Test 1");
    }

    /**
     * Then, in the test methods, just execute the specific test
     */
    public void testSomething2() throws Exception {
        myExecutionContext.execute("Test 2");
    }

    /**
     * A test that will fail
     */
    public void testThatWillFail() throws Exception {
        myExecutionContext.execute("Test 4");
    }
}
