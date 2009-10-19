/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.swing.model;

import ca.uhn.hunit.test.TestBatteryImpl;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author James
 */
public class ExecutionsTreeNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    private final TestBatteryImpl myBattery;

    public ExecutionsTreeNode(TestBatteryImpl theBattery, MyTreeModel theModel) {
        myBattery = theBattery;
    }


}
