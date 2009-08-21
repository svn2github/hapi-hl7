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
import ca.uhn.hunit.test.TestBatteryImpl;

/**
 * TODO: add!
 * 
 * @author <a href="mailto:james.agnew@uhn.on.ca">James Agnew</a>
 * @version $Revision: 1.1 $ updated on $Date: 2009-08-21 20:02:58 $ by $Author: jamesagnew $
 */
public class TestExpectNoMessageTests
{
    
    @Test
    public void testFailingTest() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_test_expect_no.xml").toURI());
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectFailure");
        
        Assert.assertTrue(ctx.getTestFailures().containsKey(battery.getTestNames2Tests().get("ExpectFailure")));
    }

    
    @Test
    public void testPassingTest() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_test_expect_no.xml").toURI());
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectFailure");
        
        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestNames2Tests().get("ExpectSuccess")));
    }

}
