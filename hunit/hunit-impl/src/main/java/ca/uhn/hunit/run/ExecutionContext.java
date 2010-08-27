/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL
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

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestEventsModel;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.log.CommonsLoggingLog;
import ca.uhn.hunit.util.log.EventCodeEnum;
import ca.uhn.hunit.util.log.ILog;
import ca.uhn.hunit.util.log.ILogProvider;
import ca.uhn.hunit.util.log.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutionContext implements IExecutionContext, Runnable {

    //~ Instance fields ------------------------------------------------------------------------------------------------
    private ExecutionStatusEnum myBatteryStatus = ExecutionStatusEnum.NOT_YET_STARTED;
    private List<IExecutionListener> myListeners = new ArrayList<IExecutionListener>();
    private List<String> myTestNamesToExecute;
    private List<TestImpl> myTestSuccesses = new ArrayList<TestImpl>();
    private Map<TestImpl, ExecutionStatusEnum> myTestExecutionStatuses = new ConcurrentHashMap<TestImpl, ExecutionStatusEnum>();
    private Map<TestImpl, TestFailureException> myTestFailures = new HashMap<TestImpl, TestFailureException>();
    private TestBatteryImpl myBattery;
    private Map<AbstractInterface, TestBatteryExecutionThread> myInterface2ExecutionThread = new HashMap<AbstractInterface, TestBatteryExecutionThread>();
    private boolean myStopped;
    private HashMap<AbstractInterface, TestBatteryExecutionThread> myInterface2thread;

    //~ Constructors ---------------------------------------------------------------------------------------------------
    /**
     * Constructor
     */
    public ExecutionContext(TestBatteryImpl theBattery) {
        myBattery = theBattery;
        myTestNamesToExecute = myBattery.getTestNames();
    }

    //~ Methods --------------------------------------------------------------------------------------------------------
    public void addFailure(TestImpl theTest, TestFailureException theException) {
    	LogFactory.INSTANCE.get(theTest).error("Failure: " + theException.getMessage(), EventCodeEnum.TEST_FAILED);
        myTestFailures.put(theTest, theException);
        myTestExecutionStatuses.put(theTest, ExecutionStatusEnum.FAILED);

        for (IExecutionListener next : myListeners) {
            next.testFailed(theTest, theException);
        }
    }

    /**
     * Adds a listeners which will be notified of execution events (test pass/fails)
     */
    public void addListener(IExecutionListener theListener) {
        myListeners.add(theListener);
    }

    public void addSuccess(TestImpl theTest) {
    	LogFactory.INSTANCE.get(theTest).info("Success!", EventCodeEnum.TEST_PASSED);
        myTestSuccesses.add(theTest);
        myTestExecutionStatuses.put(theTest, ExecutionStatusEnum.PASSED);

        for (IExecutionListener next : myListeners) {
            next.testPassed(theTest);
        }
    }

    /**
     * Convenience method which sets the test names to execute (executes all if none are passed in) and then calls {@link #run()}
     */
    public void execute(String... theTestNamesToExecute) {
        if ((theTestNamesToExecute == null) || (theTestNamesToExecute.length == 0)) {
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

    public TestBatteryImpl getBattery() {
        return myBattery;
    }

    public ExecutionStatusEnum getBatteryStatus() {
        return myBatteryStatus;
    }

    public ExecutionStatusEnum getTestExecutionStatus(TestImpl theTest) {
        return myTestExecutionStatuses.get(theTest);
    }

    public Map<TestImpl, TestFailureException> getTestFailures() {
        return myTestFailures;
    }

    public List<TestImpl> getTestSuccesses() {
        return myTestSuccesses;
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
    @Override
    public void run() {
        myStopped = false;
        LogFactory.INSTANCE.get(myBattery).info("About to execute battery");

        myBatteryStatus = ExecutionStatusEnum.RUNNING;

        for (IExecutionListener next : myListeners) {
            next.batteryStarted(myBattery);
        }

        /*
         * TODO: Use java.util.concurrent's executorservice instead of the
         * busywaits
         */
        List<TestImpl> tests = getTestsToExecute();
        myInterface2thread = new HashMap<AbstractInterface, TestBatteryExecutionThread>();

        for (TestImpl nextTest : tests) {
            myTestFailures.remove(nextTest);
            myTestSuccesses.remove(nextTest);
            myTestExecutionStatuses.put(nextTest, ExecutionStatusEnum.NOT_YET_STARTED);

            for (AbstractInterface nextInterface : nextTest.getEventsModel().getInterfaces()) {
                if (myInterface2thread.containsKey(nextInterface)) {
                    continue;
                }

                TestBatteryExecutionThread thread = new TestBatteryExecutionThread(this, nextInterface);
                myInterface2thread.put(nextInterface, thread);
                thread.start();
            }
        }

        // Wait until all threads are ready to start
        boolean stillWaiting = false;

        do {
            stillWaiting = false;

            for (TestBatteryExecutionThread next : myInterface2thread.values()) {
                if (!next.isReady()) {
                    stillWaiting = true;

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        // nothing
                    }
                }
            }
        } while (stillWaiting && !myStopped);

        if (myStopped) {
            return;
        }

        LogFactory.INSTANCE.get(myBattery).info("All interfaces are ready to proceed");

        // Start executing
        for (TestImpl nextTest : tests) {
            final ILog testLog = LogFactory.INSTANCE.get(nextTest);

            if (testLog.isInfoEnabled()) {
                testLog.info("Starting test", EventCodeEnum.TEST_STARTED);
            }

            myTestExecutionStatuses.put(nextTest, ExecutionStatusEnum.RUNNING);

            for (IExecutionListener next : myListeners) {
                next.testStarted(nextTest);
            }

            final TestEventsModel eventsModel = nextTest.getEventsModel();

            for (int eventIndex = 0; eventIndex < eventsModel.getRowCount(); eventIndex++) {
                for (AbstractInterface nextInterface : eventsModel.getInterfaces()) {
                    AbstractEvent nextEvent = eventsModel.getEventsByInterface(nextInterface).get(eventIndex);

                    if (nextEvent != null) {
                        myInterface2thread.get(nextInterface).addEvent(nextEvent);
                    }
                }
            }

            // Wait for all threads to catch up - This works but it's ugly
            boolean eventsPending;

            do {
                eventsPending = false;

                for (Entry<AbstractInterface, TestBatteryExecutionThread> next : myInterface2thread.entrySet()) {
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
            } while (!myStopped && (eventsPending == true) && !myTestFailures.containsKey(nextTest));

            // If we got out of the loop because of an error, cancel the other threads
            for (Map.Entry<AbstractInterface, TestBatteryExecutionThread> nextEntry : myInterface2thread.entrySet()) {
                nextEntry.getValue().cancelCurrentEvents();
            }

            // If we didn't fail, we succeeded :)
            if (!myTestFailures.containsKey(nextTest)) {
                addSuccess(nextTest);
            }

            if (testLog.isDebugEnabled()) {
                testLog.debug("Finished test");
            }

            if (myStopped) {
                return;
            }

        }

        // Wait until all threads are closed up
        for (TestBatteryExecutionThread next : myInterface2thread.values()) {
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

        for (Map.Entry<AbstractInterface, TestBatteryExecutionThread> next : myInterface2thread.entrySet()) {
            next.getValue().finish();
        }

        LogFactory.INSTANCE.get(myBattery).info("Finished executing battery");
    }

    public void setTestNamesToExecute(String... theTestNamesToExecute) {
        if ((theTestNamesToExecute == null) || (theTestNamesToExecute.length == 0)) {
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

    /**
     * {@inheritDoc }
     */
    @Override
    public TestBatteryExecutionThread getInterfaceExecutionThread(AbstractInterface theInterface) {
        return myInterface2ExecutionThread.get(theInterface);
    }

    @Override
    public void stop() {
        for (TestBatteryExecutionThread next : myInterface2thread.values()) {
            next.finish();
        }
        myStopped = true;
    }
}
