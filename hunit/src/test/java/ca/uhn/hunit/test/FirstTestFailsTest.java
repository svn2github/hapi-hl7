/*
 * Created on Aug 17, 2009
 */
package ca.uhn.hunit.test;

import java.io.File;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.run.ExecutionContext;

/**
 *
 * 
 * @author <a href="mailto:james.agnew@uhn.on.ca">James Agnew</a>
 * @version $Revision: 1.1 $ updated on $Date: 2009-11-01 22:31:03 $ by $Author: jamesagnew $
 */
public class FirstTestFailsTest
{

    /**
     * If first test fails, make sure subsequent tests can still pass
     */
    @Test
    public void testPassingIt() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_tests_first_failing.xml").toURI());
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute();
        
        Assert.assertTrue(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectDifferentMessage")));
        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSameMessage")));

        Assert.assertFalse(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectDifferentMessage")));
        Assert.assertFalse(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectSameMessage")));

    }


}
