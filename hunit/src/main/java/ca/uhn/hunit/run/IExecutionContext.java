/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.run;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.log.ILogProvider;

/**
 * An execution context holds all data related to the actual execution of a test
 * or battery of tests. Think of {@link TestImpl} and {@link TestBatteryImpl} as
 * models for defining tests, and execution contexts as being specific running instances
 * of those models.
 */
public interface IExecutionContext {

    /**
     * Returns the interface execution thread, if any, for a specific interface
     *
     * @param theInterface The interface
     * @return The execution thread, or null if none
     */
    TestBatteryExecutionThread getInterfaceExecutionThread(AbstractInterface<?> theInterface);


    /**
     * Requests that this execution context stop operation
     */
    void stop();

    /**
     * Returns the current battery
     */
	TestBatteryImpl getBattery();
}
