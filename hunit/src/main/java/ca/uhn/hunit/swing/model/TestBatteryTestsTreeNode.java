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
package ca.uhn.hunit.swing.model;

import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class TestBatteryTestsTreeNode extends DefaultMutableTreeNode implements TableModelListener,
                                                                                PropertyChangeListener {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final long serialVersionUID = -4977729790093086397L;

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private final MyTreeModel myModel;
    private final TestBatteryImpl myBattery;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public TestBatteryTestsTreeNode(TestBatteryImpl theBattery, MyTreeModel theModel) {
        myBattery = theBattery;
        myModel = theModel;

        myBattery.getTestModel().addTableModelListener(this);

        updateChildren();
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public void propertyChange(PropertyChangeEvent theEvt) {
        updateChildren();
        myModel.nodeStructureChanged(this);
    }

    public void tableChanged(TableModelEvent e) {
        updateChildren();
        myModel.reload(this);
    }

    private void updateChildren() {
        int index = 0;
        final List<TestImpl> tests = myBattery.getTests();

        for (TestImpl next : tests) {
            next.removePropertyChangeListener(TestImpl.NAME_PROPERTY, this);
            next.addPropertyChangeListener(TestImpl.NAME_PROPERTY, this);

            if (getChildCount() <= index) {
                TestTreeNode newChild = new TestTreeNode(myModel, next);
                add(newChild);
            } else {
                TestTreeNode nextNode = (TestTreeNode) getChildAt(index);

                if (! nextNode.getUserObject().equals(next)) {
                    TestTreeNode newChild = new TestTreeNode(myModel, next);
                    insert(newChild, index);
                }
            }

            index++;
        }

        while (getChildCount() > tests.size()) {
            remove(getChildCount() - 1);
        }
    }
}
