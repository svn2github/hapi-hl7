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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.swing.controller.ctx;

import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.run.IExecutionListener;
import ca.uhn.hunit.swing.model.ExecutionTestsTableModel;
import ca.uhn.hunit.swing.model.LogTableModel;
import ca.uhn.hunit.swing.ui.run.TestExecutionForm;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.log.LogFactory;

import java.util.Map.Entry;
import java.util.Set;

import javax.swing.SwingUtilities;

/**
 *
 * @author James
 */
public class BatteryExecutionContextController extends AbstractContextController<TestExecutionForm> {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private final ExecutionContext myExecutionContext;
    private final ExecutionTestsTableModel myExecutionModel;
    private final LogTableModel myLogTableModel;
    private final TestBatteryImpl myBattery;
    private TestExecutionForm myView;
    private final Thread myExecutionThread;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public BatteryExecutionContextController(TestBatteryImpl theBattery) {
        myBattery = theBattery;
        myExecutionContext = new ExecutionContext(theBattery);
        myExecutionContext.addListener(new MyExecutionListener());

        myExecutionModel = new ExecutionTestsTableModel(myExecutionContext);
        myLogTableModel = new LogTableModel();

        myView = new TestExecutionForm();
        myView.setController(this);

        myExecutionThread = new Thread(myExecutionContext);
        myExecutionThread.start();
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    private void batteryFinished() {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for (Entry<TestImpl, TestFailureException> next : myExecutionContext.getTestFailures().entrySet()) {
                        myView.addTestFailure(next.getKey(),
                                              next.getValue());
                    }
                }
            });
        
        myLogTableModel.stopFollowing();
    }

    public ExecutionTestsTableModel getExecutionTableModel() {
        return myExecutionModel;
    }

    public LogTableModel getLogTableModel() {
        return myLogTableModel;
    }

    @Override
    public TestExecutionForm getView() {
        return myView;
    }

    //~ Inner Classes --------------------------------------------------------------------------------------------------

    private class MyExecutionListener implements IExecutionListener {
        public void batteryFailed(TestBatteryImpl theBattery) {
            batteryFinished();
        }

        public void batteryPassed(TestBatteryImpl theBattery) {
            batteryFinished();
        }

        public void batteryStarted(TestBatteryImpl theBattery) {
            // nothing
        }

        public void testFailed(TestImpl theTest, TestFailureException theException) {
            // nothing
        }

        public void testPassed(TestImpl theTest) {
            // nothing
        }

        public void testStarted(TestImpl theTest) {
            // nothing
        }
    }
}
