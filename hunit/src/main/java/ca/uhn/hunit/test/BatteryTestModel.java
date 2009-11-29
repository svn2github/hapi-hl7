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
package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.xsd.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author James
 */
public class BatteryTestModel extends AbstractTableModel {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private final List<TestImpl> myTests = new ArrayList<TestImpl>();
    private final Map<String, TestImpl> myTestNames2Tests = new HashMap<String, TestImpl>();
    private final TestBatteryImpl myBattery;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public BatteryTestModel(TestBatteryImpl theBattery) {
        myBattery = theBattery;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * Adds a new test to the model
     */
    void addTest(TestImpl test) {
        myTests.add(test);
        fireTableRowsInserted(myTests.size() - 1, myTests.size() - 1);
    }

    public int getColumnCount() {
        return 1;
    }

    public int getRowCount() {
        return myTests.size();
    }

    public TestImpl getTestByName(String theName) {
        return myTestNames2Tests.get(theName);
    }

    public List<String> getTestNames() {
        ArrayList<String> retVal = new ArrayList<String>();

        for (TestImpl next : myTests) {
            retVal.add(next.getName());
        }

        return retVal;
    }

    public List<TestImpl> getTests() {
        return Collections.unmodifiableList(myTests);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return myTests.get(rowIndex);
    }

    public void initFromXml(List<Test> theTestList) throws ConfigurationException {
        myTestNames2Tests.clear();
        myTests.clear();

        for (Test next : theTestList) {
            TestImpl nextTest = new TestImpl(myBattery, next);

            if (myTestNames2Tests.containsKey(nextTest.getName())) {
                throw new ConfigurationException("Duplicate test name detected: " + nextTest.getName());
            }

            myTestNames2Tests.put(nextTest.getName(),
                                  nextTest);
            myTests.add(nextTest);
        }

        fireTableStructureChanged();
    }
}
