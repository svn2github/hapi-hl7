/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * The Initial Developer of the Original Code is University Health Network. Copyright (C)
 * 2001.  All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the  "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */
package ca.uhn.hunit.run;

import ca.uhn.hunit.test.TestEventsModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.log.CommonsLoggingLog;
import ca.uhn.hunit.util.log.ILogProvider;
import java.util.Collections;

public class ExecutionContext implements Runnable {

    private Map<TestImpl, TestFailureException> myTestFailures = new HashMap<TestImpl, TestFailureException>();
    private List<TestImpl> myTestSuccesses = new ArrayList<TestImpl>();
    private List<IExecutionListener> myListeners = new ArrayList<IExecutionListener>();
    private TestBatteryImpl myBattery;
    private ILogProvider myLog = new CommonsLoggingLog();
    private List<String> myTestNamesToExecute;
    private Map<TestImpl, ExecutionStatusEnum> myTestExecutionStatuses = Collections.synchronizedMap(new HashMap<TestImpl, ExecutionStatusEnum>());
    private ExecutionStatusEnum myBatteryStatus = ExecutionStatusEnum.NOT_YET_STARTED;

    /**
     * Constructor
     */
    public ExecutionContext(TestBatteryImpl theBattery) {
        myBattery = theBattery;
        myTestNamesToExecute = myBattery.getTestNames();
    }

    public ExecutionStatusEnum getTestExecutionStatus(TestImpl theTest) {
        return myTestExecutionStatuses.get(theTest);
    }

    /**
     * Adds a listeners which will be notified of execution events (test pass/fails)
     */
    public void addListener(IExecutionListener theListener) {
        myListeners.add(theListener);
    }

    public void addFailure(TestImpl theTest, TestFailureException theException) {
        myLog.get(theTest).error("Failure: " + theException.getMessage());
        myTestFailures.put(theTest, theException);
        myTestExecutionStatuses.put(theTest, ExecutionStatusEnum.FAILED);

        for (IExecutionListener next : myListeners) {
            next.testFailed(theTest, theException);
        }

    }

    public void addSuccess(TestImpl theTest) {
        myLog.get(theTest).info("Success!");
        myTestSuccesses.add(theTest);
        myTestExecutionStatuses.put(theTest, ExecutionStatusEnum.PASSED);

        for (IExecutionListener next : myListeners) {
            next.testPassed(theTest);
        }

    }

    public ExecutionStatusEnum getBatteryStatus() {
        return myBatteryStatus;
    }

    public Map<TestImpl, TestFailureException> getTestFailures() {
        return myTestFailures;
    }

    public List<TestImpl> getTestSuccesses() {
        return myTestSuccesses;
    }

    /**
     * Convenience method which sets the test names to execute and then calls {@link #run()}
     */
    public void execute(String... theTestNamesToExecute) {
        if (theTestNamesToExecute == null || theTestNamesToExecute.length == 0) {
            setTestNamesToExecute((List<String>) null);
        } else {
            List<String> testNames = new ArrayList<String>(Arrays.asList(theTestNamesToExecute));
            setTestNamesToExecute(testNames);
        }
        run();
    }

    /**
     * Convenience method which sets the test names to execute and then calls {@link #run()}
     */
    public void execute(List<String> theTestNamesToExecute) {
        setTestNamesToExecute(theTestNamesToExecute);
        run();
    }

    public void setTestNamesToExecute(String... theTestNamesToExecute) {
        if (theTestNamesToExecute == null || theTestNamesToExecute.length == 0) {
            setTestNamesToExecute((List<String>) null);
        } else {
            List<String> testNames = new ArrayList<String>(Arrays.asList(theTestNamesToExecute));
            setTestNamesToExecute(testNames);
        }
    }

    public void setTestNamesToExecute(List<String> theTestNamesToExecute) {
        if (theTestNamesToExecute == null) {
            theTestNamesToExecute = myBattery.getTestNames();
        }

        myTestNamesToExecute = theTestNamesToExecute;
    }

    public TestBatteryImpl getBattery() {
        return myBattery;
    }

    public List<TestImpl> getTestsToExecute() {
        ArrayList<TestImpl> retVal = new ArrayList<TestImpl>();
        for (String nextName : myTestNamesToExecute) {
            final TestImpl testByName = myBattery.getTestByName(nextName);
            if (testByName != null) {
                retVal.add(testByName);
            }
        }
        return retVal;
    }

    /**
     * Begins execution
     */
    public void run() {
        myLog.get(myBattery).info("About to execute battery");

        myBatteryStatus = ExecutionStatusEnum.RUNNING;
        for (IExecutionListener next : myListeners) {
            next.batteryStarted(myBattery);
        }

        /* 
         * TODO: Use java.util.concurrent's executorservice instead of the
         * busywaits
         */

        List<TestImpl> tests = getTestsToExecute();
        Map<String, TestBatteryExecutionThread> interface2thread = new HashMap<String, TestBatteryExecutionThread>();
        for (TestImpl nextTest : tests) {

            myTestFailures.remove(nextTest);
            myTestSuccesses.remove(nextTest);
            myTestExecutionStatuses.put(nextTest, ExecutionStatusEnum.NOT_YET_STARTED);

            for (String nextInterfaceId : nextTest.getEventsModel().getInterfaceIds()) {
                if (interface2thread.containsKey(nextInterfaceId)) {
                    continue;
                }

                AbstractInterface nextInterface;
                try {
                    nextInterface = myBattery.getInterface(nextInterfaceId);
                } catch (ConfigurationException e) {
                    final String message = "Unknown interface ID[" + nextInterfaceId + "]. This should have already been caught, this is a bug";
                    myLog.getSystem(getClass()).error(message, e);
                    throw new Error(message);
                }
                TestBatteryExecutionThread thread = new TestBatteryExecutionThread(this, nextInterface);
                interface2thread.put(nextInterfaceId, thread);
                thread.start();

            }

        }

        // Wait until all threads are ready to start
        for (TestBatteryExecutionThread next : interface2thread.values()) {
            if (!next.isReady()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    // nothing
                }
            }
        }

        // Start executing
        for (TestImpl nextTest : tests) {

            myTestExecutionStatuses.put(nextTest, ExecutionStatusEnum.RUNNING);
            for (IExecutionListener next : myListeners) {
                next.testStarted(nextTest);
            }

            final TestEventsModel eventsModel = nextTest.getEventsModel();
            for (int eventIndex = 0; eventIndex < eventsModel.getRowCount(); eventIndex++) {
                for (String nextInterfaceId : eventsModel.getInterfaceIds()) {
                    AbstractEvent nextEvent = eventsModel.getEventsByInterfaceId(nextInterfaceId).get(eventIndex);
                    if (nextEvent != null) {
                        interface2thread.get(nextInterfaceId).addEvent(nextEvent);
                    }
                }
            }

            // Wait for all threads to catch up - This works but it's ugly
            boolean eventsPending;
            do {
                eventsPending = false;
                for (Entry<String, TestBatteryExecutionThread> next : interface2thread.entrySet()) {
                    TestBatteryExecutionThread nextThread = next.getValue();
                    if (nextThread.hasEventsPending()) {
                        eventsPending = true;
                    }
                    if (myTestFailures.containsKey(nextTest)) {
                        break;
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        // nothing
                    }
                }
            } while (eventsPending == true && !myTestFailures.containsKey(nextTest));

            // If we got out of the loop because of an error, cancel the other threads
            for (Map.Entry<String, TestBatteryExecutionThread> nextEntry : interface2thread.entrySet()) {
                nextEntry.getValue().cancelCurrentEvents();
            }

            // If we didn't fail, we succeeded :)
            if (!myTestFailures.containsKey(nextTest)) {
                addSuccess(nextTest);
            }

        }


        // Wait until all threads are closed up
        for (TestBatteryExecutionThread next : interface2thread.values()) {
            if (next.isReady()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    // nothing
                }
            }
        }

        if (myTestFailures.size() > 0) {
            myBatteryStatus = ExecutionStatusEnum.FAILED;
            for (IExecutionListener next : myListeners) {
                next.batteryFailed(myBattery);
            }
        } else {
            myBatteryStatus = ExecutionStatusEnum.PASSED;
            for (IExecutionListener next : myListeners) {
                next.batteryPassed(myBattery);
            }
        }

        for (TestBatteryExecutionThread next : interface2thread.values()) {
            next.finish();
        }

        myLog.get(myBattery).info("Finished executing battery");
    }

    public ILogProvider getLog() {
        return myLog;
    }

    public void setLog(ILogProvider myLog) {
        this.myLog = myLog;
    }
}
