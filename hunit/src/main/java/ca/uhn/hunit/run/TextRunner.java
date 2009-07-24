package ca.uhn.hunit.run;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
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
		ExecutionContext ctx = new ExecutionContext();
		batteryImpl.execute(ctx);
		
		System.out.println("----------------------------------------------------");
		
		if (!ctx.getBatteryFailures().isEmpty()) {
			System.out.println("Warning, the following batteries failed:");
			for (Map.Entry<TestBatteryImpl, TestFailureException> next : ctx.getBatteryFailures().entrySet()) {
				System.out.println("\r\n * " + next.getKey().getName() +" - Reason: " + next.getValue().describeReason());
			}
			System.out.println("\r\n");
		}

		if (!ctx.getTestFailures().isEmpty()) {
			System.out.println("Warning, the following tests failed:");
			for (Map.Entry<TestImpl, TestFailureException> next : ctx.getTestFailures().entrySet()) {
				System.out.println("\r\n * " + next.getKey().getName() +" - Reason: " + next.getValue().describeReason());
			}
			System.out.println("\r\n");
		}
	}

}
