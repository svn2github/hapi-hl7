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
import org.springframework.core.io.ClassPathResource;


public class Hl7V2Test {

	@Test
	public void testSuccessfulExpectSpecific() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
		new MllpHl7v2MessageSwapper(true, "LEIGHTON", "TEST").start();
		
		ClassPathResource defFile = new ClassPathResource("unit_tests_hl7.xml");
		TestBatteryImpl battery = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext(battery);
		ctx.execute("ExpectSpecific Test");
		
		Assert.assertFalse(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectSpecific Test")));
		Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSpecific Test")));
	}

	@Test
	public void testFailureExpectSpecific() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
		new MllpHl7v2MessageSwapper(true, "LEIGHTON", "TEST2").start();
		
		ClassPathResource defFile = new ClassPathResource("unit_tests_hl7.xml");
		TestBatteryImpl battery = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext(battery);
		ctx.execute("ExpectSpecific Test");
		
		TestImpl test = battery.getTestByName("ExpectSpecific Test");
		Assert.assertFalse(ctx.getTestSuccesses().contains(test));
		Assert.assertTrue(ctx.getTestFailures().containsKey(test));
	}


	@Test
	public void testMultipleTests() throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
		new MllpHl7v2MessageSwapper(true, "LEIGHTON", "TEST", 2).start();
		
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

	}


	
}
