/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.run.ExecutionContext;
import java.io.File;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBException;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author James
 */
public class XmlTest {

   	@Test
	public void testSuccessfulExpectSpecific() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {

        File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_tests_xml.xml").toURI());
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
