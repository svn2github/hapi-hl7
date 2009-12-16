/*
 * Created on Aug 17, 2009
 */
package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.iface.StaticActiveMQConnectionFactory;
import ca.uhn.hunit.run.ExecutionContext;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

/**
 *
 *
 * @author <a href="mailto:james.agnew@uhn.on.ca">James Agnew</a>
 * @version $Revision: 1.4 $ updated on $Date: 2009-12-16 17:20:48 $ by $Author: jamesagnew $
 */
public class FirstTestFailsTest {
    //~ Methods --------------------------------------------------------------------------------------------------------

    @After
    public void cleanup() {
        StaticActiveMQConnectionFactory.reset();
    }

    /**
     * If first test fails, make sure subsequent tests can still pass
     */
    @Test
    public void testPassingIt()
                       throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        ClassPathResource defFile = new ClassPathResource("unit_tests_first_failing.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute();

        Assert.assertTrue(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectDifferentMessage")));
        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSameMessage")));

        Assert.assertFalse(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectDifferentMessage")));
        Assert.assertFalse(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectSameMessage")));
    }


    public static void main(String[] args) throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        new FirstTestFailsTest().testPassingIt();
    }

}
