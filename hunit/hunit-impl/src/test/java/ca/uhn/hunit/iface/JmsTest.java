/*
 * Created on Aug 17, 2009
 */
package ca.uhn.hunit.iface;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.example.MllpHl7v2MessageSwapper;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;

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
 * @author <a href="mailto:jamesagnew@users.sourceforge.net">James Agnew</a>
 * @version $Revision: 1.2 $ updated on $Date: 2011-03-21 12:52:00 $ by $Author: jamesagnew $
 */
public class JmsTest {
    //~ Methods --------------------------------------------------------------------------------------------------------

    @After
    public void cleanup() {
        StaticActiveMQConnectionFactory.reset();
    }

    @Test
    public void testFailingTest()
                         throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        ClassPathResource defFile = new ClassPathResource("unit_tests_jms.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectDifferentMessage");

        Assert.assertTrue(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectDifferentMessage")));
    }

    @Test
    public void testPassingTest()
                         throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        ClassPathResource defFile = new ClassPathResource("unit_tests_jms.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSameMessage");

        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSameMessage")));
    }
}
