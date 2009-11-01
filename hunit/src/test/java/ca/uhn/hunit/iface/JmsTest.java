/*
 * Created on Aug 17, 2009
 */
package ca.uhn.hunit.iface;

import java.io.File;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.example.MllpHl7v2MessageSwapper;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;

/**
 * 
 * 
 * @author <a href="mailto:jamesagnew@users.sourceforge.net">James Agnew</a>
 * @version $Revision: 1.3 $ updated on $Date: 2009-11-01 22:31:04 $ by $Author: jamesagnew $
 */
public class JmsTest
{
    
    @Test
    public void testPassingTest() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_tests_jms.xml").toURI());
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSameMessage");
        
        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSameMessage")));
    }

    @Test
    public void testFailingTest() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_tests_jms.xml").toURI());
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectDifferentMessage");
        
        Assert.assertTrue(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectDifferentMessage")));
    }
    

}
