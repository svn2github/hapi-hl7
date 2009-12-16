package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.example.MllpHl7v2MessageSwapper;
import ca.uhn.hunit.run.ExecutionContext;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;

import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Hl7V2Test {
    private final Log ourLog = LogFactory.getLog(Hl7V2Test.class);

    //~ Methods --------------------------------------------------------------------------------------------------------

    @Test
    public void testFailureExpectSpecific()
                                   throws URISyntaxException, InterfaceWontStartException, ConfigurationException,
                                          JAXBException {
        MllpHl7v2MessageSwapper swapper = new MllpHl7v2MessageSwapper(true, "LEIGHTON", "TEST2");
        swapper.start();

        ClassPathResource defFile = new ClassPathResource("unit_tests_hl7.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSpecific Test");

        TestImpl test = battery.getTestByName("ExpectSpecific Test");
        Assert.assertFalse(ctx.getTestSuccesses().contains(test));
        Assert.assertTrue(ctx.getTestFailures().containsKey(test));

        ourLog.info("Waiting for swapper to stop");
        swapper.waitForStopped();
        ourLog.info("Finished test");
    }

    @Test
    public void testMultipleTests()
                           throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        MllpHl7v2MessageSwapper swapper = new MllpHl7v2MessageSwapper(true, "LEIGHTON", "TEST", 2);
        swapper.start();

        ClassPathResource defFile = new ClassPathResource("unit_tests_hl7.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSpecific Test", "ExpectSecond Test");

        TestImpl test = battery.getTestByName("ExpectSpecific Test");
        Assert.assertTrue(ctx.getTestSuccesses().contains(test));
        Assert.assertFalse(ctx.getTestFailures().containsKey(test));

        test = battery.getTestByName("ExpectSecond Test");
        Assert.assertFalse(ctx.getTestSuccesses().contains(test));
        Assert.assertTrue(ctx.getTestFailures().containsKey(test));

        ourLog.info("Waiting for swapper to stop");
        swapper.waitForStopped();
        ourLog.info("Finished test");
    }

    @Test
    public void testSuccessfulExpectSpecific()
                                      throws URISyntaxException, InterfaceWontStartException, ConfigurationException,
                                             JAXBException {
        MllpHl7v2MessageSwapper swapper = new MllpHl7v2MessageSwapper(true, "LEIGHTON", "TEST");
        swapper.start();

        ClassPathResource defFile = new ClassPathResource("unit_tests_hl7.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSpecific Test");

        Assert.assertFalse(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectSpecific Test")));
        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSpecific Test")));


        ourLog.info("Waiting for swapper to stop");
        swapper.waitForStopped();
        ourLog.info("Finished test");

    }


    public static void main(String[] args) throws Exception {
        final Hl7V2Test hl7V2Test = new Hl7V2Test();

//        hl7V2Test.testFailureExpectSpecific();
//        hl7V2Test.testMultipleTests();
        hl7V2Test.testSuccessfulExpectSpecific();
    }
}
