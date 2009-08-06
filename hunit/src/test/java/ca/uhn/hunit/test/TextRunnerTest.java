package ca.uhn.hunit.test;

import java.io.File;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.example.MllpHl7v2MessageSwapper;
import ca.uhn.hunit.run.TextRunner;


public class TextRunnerTest {

	@Test
	public void testTextRunnerSuccess() throws URISyntaxException, InterfaceWontStartException, JAXBException, ConfigurationException {
		new MllpHl7v2MessageSwapper(false, "LEIGHTON", "TEST2", 2).start();
		
		File defFile = new File(Thread.currentThread().getContextClassLoader().getResource("unit_tests_hl7.xml").toURI());
		TextRunner.main(new String[] {defFile.getAbsolutePath()});
	}
	
}
