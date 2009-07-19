package ca.uhn.hunit.run;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.test.TestBatteryImpl;

public class TextRunner {

	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws JAXBException 
	 * @throws ConfigurationException 
	 * @throws InterfaceWontStartException 
	 */
	public static void main(String[] args) throws URISyntaxException, JAXBException, InterfaceWontStartException, ConfigurationException {
		
		String defFileName = args[0];
		URL defFileUrl = TextRunner.class.getClassLoader().getResource(defFileName);
		File defFile = new File(defFileUrl.toURI());
		
		TestBatteryImpl batteryImpl = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext();
		batteryImpl.execute(ctx);
	}

}
