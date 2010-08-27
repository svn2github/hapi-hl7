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

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.msg.XmlMessageImpl;
import ca.uhn.hunit.swing.ui.ImageFactory;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class InterfacesTreeRenderer extends DefaultTreeCellRenderer {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    //~ Methods --------------------------------------------------------------------------------------------------------

    @Override
    public Component getTreeCellRendererComponent(JTree theTree, Object theValue, boolean theSelected,
                                                  boolean theExpanded, boolean theLeaf, int theRow, boolean theHasFocus) {
        super.getTreeCellRendererComponent(theTree, theValue, theSelected, theExpanded, theLeaf, theRow, theHasFocus);
        updateContent(theValue);

        return this;
    }

    private void updateContent(Object theValue) {
        setIcon(null);

        if (theValue instanceof TestBatteryTreeNode) {
            TestBatteryTreeNode node = (TestBatteryTreeNode) theValue;
            TestBatteryImpl battery = (TestBatteryImpl) node.getUserObject();
            setText(battery.getName());
        } else if (theValue instanceof ExecutionsTreeNode) {
            setText("Executions");
        } else if (theValue instanceof TestBatteryInterfacesTreeNode) {
            setText("Interfaces");
        } else if (theValue instanceof EventTreeNode) {
        	EventTreeNode node = (EventTreeNode)theValue;
            setText(node.getDescription());
        } else if (theValue instanceof TestBatteryTestsTreeNode) {
            setText("Tests");
        } else if (theValue instanceof InterfaceTreeNode) {
            InterfaceTreeNode node = (InterfaceTreeNode) theValue;
            AbstractInterface<?> ai = (AbstractInterface<?>) node.getUserObject();
            String name = ai.getId();
            setText(name);

            if (ai.isStarted()) {
                setIcon(ImageFactory.getInterfaceOn());
            } else {
                setIcon(ImageFactory.getInterfaceOff());
            }
        } else if (theValue instanceof MessageTreeNode) {
            MessageTreeNode node = (MessageTreeNode) theValue;
            AbstractMessage<?> ai = (AbstractMessage<?>) node.getUserObject();
            String name = node.getName();
            setText(name);

            if (ai instanceof Hl7V2MessageImpl) {
                setIcon(ImageFactory.getMessageHl7());
            } else if (ai instanceof XmlMessageImpl) {
                setIcon(ImageFactory.getMessageXml());
            }
        } else if (theValue instanceof TestTreeNode) {
            TestTreeNode node = (TestTreeNode) theValue;
            TestImpl ai = (TestImpl) node.getUserObject();
            String name = ai.getName();
            setText(name);
            setIcon(ImageFactory.getTest());
        } else {
            setText("--" + theValue.toString());
        }
    }
}
