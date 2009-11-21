/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.iface.StaticActiveMQConnectionFactory;
import ca.uhn.hunit.run.ExecutionContext;
import java.io.File;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBException;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author James
 */
public class XmlTest {
    @After
    public void cleanup() {
        StaticActiveMQConnectionFactory.reset();
    }

   	@Test
	public void testSuccessfulExpectSpecific() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        ClassPathResource defFile = new ClassPathResource("unit_tests_xml.xml");
		TestBatteryImpl battery = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext(battery);
		ctx.execute("ExpectSameMessage", "ExpectDifferentMessage");

		Assert.assertTrue(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectDifferentMessage")));
		Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSameMessage")));

        String failReason = ctx.getTestFailures().get(battery.getTestByName("ExpectDifferentMessage")).describeReason();
        System.out.println(failReason);

	}


    public static void main(String[] args) throws Exception {
        new XmlTest().testSuccessfulExpectSpecific();
    }

}
