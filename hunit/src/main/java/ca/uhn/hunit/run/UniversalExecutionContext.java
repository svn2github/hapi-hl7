/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.run;

import ca.uhn.hunit.iface.AbstractInterface;
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

    private Map<AbstractInterface, TestBatteryExecutionThread> myInterface2ExecutionThread = new ConcurrentHashMap<AbstractInterface, TestBatteryExecutionThread>();

    /**
     * {@inheritDoc }
     */
    @Override
    public TestBatteryExecutionThread getInterfaceExecutionThread(AbstractInterface theInterface) {
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

    

}
