package ca.uhn.hunit.test;

import java.io.File;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.example.MllpHl7v2MessageSwapper;
import ca.uhn.hunit.run.ExecutionContext;


public class Hl7V2Test {

	@Test
	public void testSuccessfulExpectSpecific() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
		startMessageReplacer("LEIGHTON", "TEST");
		
		File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_tests_hl7.xml").toURI());
		TestBatteryImpl battery = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext();
		battery.execute(ctx, "ExpectSpecific Test");
		
		Assert.assertFalse(ctx.getBatteryFailures().containsKey(battery.getTestNames2Tests().get("ExpectSpecific Test")));
		Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestNames2Tests().get("ExpectSpecific Test")));
	}

	@Test
	public void testFailureExpectSpecific() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
		startMessageReplacer("LEIGHTON", "TEST2");
		
		File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_tests_hl7.xml").toURI());
		TestBatteryImpl battery = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext();
		battery.execute(ctx, "ExpectSpecific Test");
		
		ITest test = battery.getTestNames2Tests().get("ExpectSpecific Test");
		Assert.assertFalse(ctx.getTestSuccesses().contains(test));
		Assert.assertTrue(ctx.getTestFailures().containsKey(test));
	}

	public static void startMessageReplacer(String theOldValue, final String theNewValue) {
		new MllpHl7v2MessageSwapper(theOldValue, theNewValue).start();
	}
	
}
