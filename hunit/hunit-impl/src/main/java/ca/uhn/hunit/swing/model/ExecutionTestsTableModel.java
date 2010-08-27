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
package ca.uhn.hunit.swing.model;

import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.run.ExecutionStatusEnum;
import ca.uhn.hunit.run.IExecutionListener;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;

import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author James
 */
public class ExecutionTestsTableModel extends AbstractTableModel implements IExecutionListener {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    /** Column index */
    public static final int COLUMN_BATTERY = 0;

    /** Column index */
    public static final int COLUMN_STATUS = 2;

    /** Column index */
    public static final int COLUMN_TEST = 1;
    private static final long serialVersionUID = 1L;

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private final ArrayList<TestBatteryImpl> myBatteryColumn;
    private final ArrayList<ExecutionStatusEnum> myStatusColumn;
    private final ArrayList<TestImpl> myTestsColumn;
    private final ExecutionContext myCtx;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public ExecutionTestsTableModel(ExecutionContext theCtx) {
        myCtx = theCtx;
        myCtx.addListener(this);

        myBatteryColumn = new ArrayList<TestBatteryImpl>();
        myTestsColumn = new ArrayList<TestImpl>();
        myStatusColumn = new ArrayList<ExecutionStatusEnum>();

        myBatteryColumn.add(myCtx.getBattery());
        myTestsColumn.add(null);
        myStatusColumn.add(null);

        for (TestImpl nextTest : myCtx.getTestsToExecute()) {
            myBatteryColumn.add(null);
            myTestsColumn.add(nextTest);
            myStatusColumn.add(myCtx.getTestExecutionStatus(nextTest));
        }
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public void batteryFailed(TestBatteryImpl theBattery) {
        update(theBattery);
    }

    public void batteryPassed(TestBatteryImpl theBattery) {
        update(theBattery);
    }

    public void batteryStarted(TestBatteryImpl theBattery) {
        update(theBattery);
    }

    /**
     * {@inheritDoc }
     */
    public int getColumnCount() {
        return 3;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COLUMN_BATTERY:
                return "Battery";

            case COLUMN_TEST:
                return "Test";

            case COLUMN_STATUS:
                return "Status";

            default:
                throw new IllegalArgumentException("" + column);
        }
    }

    /**
     * {@inheritDoc }
     */
    public int getRowCount() {
        return myCtx.getTestsToExecute().size() + COLUMN_TEST;
    }

    /**
     * {@inheritDoc }
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case COLUMN_BATTERY:
                return myBatteryColumn.get(rowIndex);

            case COLUMN_TEST:
                return myTestsColumn.get(rowIndex);

            case COLUMN_STATUS:
                return myStatusColumn.get(rowIndex);

            default:
                throw new IllegalArgumentException("" + columnIndex);
        }
    }

    public void testFailed(TestImpl theTest, TestFailureException theException) {
        update(theTest);
    }

    public void testPassed(TestImpl theTest) {
        update(theTest);
    }

    public void testStarted(TestImpl theTest) {
        update(theTest);
    }

    private void update(final TestImpl theTest) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int index = myTestsColumn.indexOf(theTest);
                    myStatusColumn.set(index,
                                       myCtx.getTestExecutionStatus(theTest));
                    fireTableCellUpdated(index, COLUMN_STATUS);
                }
            });
    }

    private void update(final TestBatteryImpl theBattery) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int index = myBatteryColumn.indexOf(theBattery);
                    myStatusColumn.set(index,
                                       myCtx.getBatteryStatus());
                    fireTableCellUpdated(index, COLUMN_STATUS);
                }
            });
    }
}
