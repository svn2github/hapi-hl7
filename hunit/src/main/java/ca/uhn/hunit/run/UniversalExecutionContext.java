/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.run;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.util.log.CommonsLoggingLog;
import ca.uhn.hunit.util.log.ILogProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines an execution context which is supposed to act as a parent for a
 * specific instance of a battery exection.
 *
 * This context will hold, for instance, interface
 * execution threads for the Swing UI if the user has requested that they
 * run outside of a specific text/battery exection
 */
public class UniversalExecutionContext implements IExecutionContext {

    private Map<AbstractInterface<?>, TestBatteryExecutionThread> myInterface2ExecutionThread = new ConcurrentHashMap<AbstractInterface<?>, TestBatteryExecutionThread>();
    private ILogProvider myLog = new CommonsLoggingLog();
	private TestBatteryImpl myBattery;

	/**
	 * Constructor
	 * 
	 * @param theBattery The test battery
	 */
    public UniversalExecutionContext(TestBatteryImpl theBattery) {
    	myBattery = theBattery;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public TestBatteryExecutionThread getInterfaceExecutionThread(AbstractInterface<?> theInterface) {
        return myInterface2ExecutionThread.get(theInterface);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void stop() {
        for (TestBatteryExecutionThread next : myInterface2ExecutionThread.values()) {
            next.finish();
        }
    }

	@Override
	public TestBatteryImpl getBattery() {
		return myBattery;
	}

    

}
