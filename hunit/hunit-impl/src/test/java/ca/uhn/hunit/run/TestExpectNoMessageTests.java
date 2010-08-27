/*
 * Created on Aug 17, 2009
 */
package ca.uhn.hunit.run;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.example.MllpHl7v2MessageSwapper;
import ca.uhn.hunit.iface.Hl7V2Test;
import ca.uhn.hunit.iface.StaticActiveMQConnectionFactory;
import ca.uhn.hunit.test.TestBatteryImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

/**
 * TODO: add!
 *
 * @author <a href="mailto:james.agnew@uhn.on.ca">James Agnew</a>
 * @version $Revision: 1.1 $ updated on $Date: 2010-08-27 15:12:02 $ by $Author: jamesagnew $
 */
public class TestExpectNoMessageTests {
    private static final Log ourLog = LogFactory.getLog(TestExpectNoMessageTests.class);

    //~ Methods --------------------------------------------------------------------------------------------------------

    @After
    public void cleanup() {
        StaticActiveMQConnectionFactory.reset();
    }

    public static void main(String[] args)
                     throws InterfaceWontStartException, ConfigurationException, URISyntaxException, JAXBException {
        TestExpectNoMessageTests t = new TestExpectNoMessageTests();
        t.testFailingTest();
        t.testPassingTest();
    }

    @Test
    public void testFailingTest()
                         throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        ClassPathResource defFile = new ClassPathResource("unit_test_expect_no.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectFailure");

        Assert.assertTrue(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectFailure")));
    }

    @Test
    public void testPassingTest()
                         throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        ClassPathResource defFile = new ClassPathResource("unit_test_expect_no.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSuccess");

        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSuccess")));
    }
    
    @Test
    public void testPassingTestWithOtherExpectAfter() throws ConfigurationException, JAXBException {
        ClassPathResource defFile = new ClassPathResource("unit_test_expect_no.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectWithTestAfter1", "ExpectWithTestAfter2");

        Assert.assertFalse(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectWithTestAfter1")));
        Assert.assertFalse(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectWithTestAfter2")));

        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectWithTestAfter1")));
        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectWithTestAfter2")));

    }
    
}
