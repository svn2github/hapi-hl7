/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.example;

import ca.uhn.hunit.junit.*;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;
import java.io.File;
import junit.framework.TestCase;
import org.springframework.util.ResourceUtils;

/**
 * This class gives an example of how to create a JUnit 3 test
 */
public class Junit3ExampleTest extends TestCase {

    private static ExecutionContext ourExecutionContext;

    /**
     * In the setup method, prepare the test
     */
    public void setUp() throws Exception {

        /*
         * In the setup method, load the XML test file you are using. Here we are
         * using a Spring Framework utility class to load a File from the classpath,
         * but any other way of getting a file reference works too.
         */
        File file = ResourceUtils.getFile("classpath:ca/uhn/hunit/junit/unit_tests_many_passing.xml");
        TestBatteryImpl battery = new TestBatteryImpl(file);

        // We store the execution context in a static variable, so that open
        // interfaces are kept open between tests. This might or might not be
        // desirable, depending on your circumstances
        if (ourExecutionContext == null) {
            ourExecutionContext = new ExecutionContext(battery);

            // This listener generates JUnit 3 assert failures if a test fails
            ourExecutionContext.addListener(new Junit3FailureListener());
        }

    }

    /**
     * Then, in the test methods, just execute the specific test
     */
    public void testSomething1() throws Exception {
        ourExecutionContext.execute("Test 1");
    }

    /**
     * Then, in the test methods, just execute the specific test
     */
    public void testSomething2() throws Exception {
        ourExecutionContext.execute("Test 2");
    }

    /**
     * A test that will fail
     */
    public void testThatWillFail() throws Exception {
        ourExecutionContext.execute("Test 4");
    }

}
