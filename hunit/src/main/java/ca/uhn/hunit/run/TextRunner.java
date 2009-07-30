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
		
		
		ctx.getLog().info(batteryImpl, "----------------------------------------------------");
        ctx.getLog().info(batteryImpl, "The following tests passed:");
        for (TestImpl next : ctx.getTestSuccesses()) {
            ctx.getLog().info(batteryImpl, " * " + next.getName());
        }
        ctx.getLog().info(batteryImpl, "----------------------------------------------------");
		
		if (!ctx.getTestFailures().isEmpty()) {
		    ctx.getLog().info(batteryImpl, "Warning, the some tests failed!");
			for (Map.Entry<TestImpl, TestFailureException> next : ctx.getTestFailures().entrySet()) {
			    ctx.getLog().info(batteryImpl, "The following test failed: " + next.getKey().getName() +" - Reason: " + next.getValue().describeReason());
			}
		}
	}

}
