/*
 * Created on Aug 17, 2009
 */
package ca.uhn.hunit.run;

import java.io.File;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.iface.StaticActiveMQConnectionFactory;
import ca.uhn.hunit.test.TestBatteryImpl;
import org.junit.After;
import org.springframework.core.io.ClassPathResource;

/**
 * TODO: add!
 * 
 * @author <a href="mailto:james.agnew@uhn.on.ca">James Agnew</a>
 * @version $Revision: 1.4 $ updated on $Date: 2009-11-21 18:29:30 $ by $Author: jamesagnew $
 */
public class TestExpectNoMessageTests
{
    @After
    public void cleanup() {
        StaticActiveMQConnectionFactory.reset();
    }

    @Test
    public void testFailingTest() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        ClassPathResource defFile = new ClassPathResource("unit_test_expect_no.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectFailure");
        
        Assert.assertTrue(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectFailure")));
    }

    
    @Test
    public void testPassingTest() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        ClassPathResource defFile = new ClassPathResource("unit_test_expect_no.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSuccess");
        
        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSuccess")));
    }

    public static void main(String[] args) throws InterfaceWontStartException, ConfigurationException, URISyntaxException, JAXBException {
        TestExpectNoMessageTests t = new TestExpectNoMessageTests();
        t.testFailingTest();
        t.testPassingTest();
    }

}
