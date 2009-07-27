package ca.uhn.hunit.run;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;

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
		File defFile = new File(defFileName);
		
		TestBatteryImpl batteryImpl = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext(batteryImpl);
		ctx.execute();
		
		
		System.out.flush();
		System.out.println("----------------------------------------------------");
		
		if (!ctx.getTestFailures().isEmpty()) {
			System.out.println("Warning, the following tests failed:");
			for (Map.Entry<TestImpl, TestFailureException> next : ctx.getTestFailures().entrySet()) {
				System.out.println("\r\n * " + next.getKey().getName() +" - Reason: " + next.getValue().describeReason());
			}
			System.out.println("\r\n");
		}
	}

}
