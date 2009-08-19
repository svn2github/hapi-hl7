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
package ca.uhn.hunit.swing.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;

public class TestBatteryInterfacesTreeNode extends DefaultMutableTreeNode implements PropertyChangeListener {

	private static final long serialVersionUID = -4977729790093086397L;
	
	public static final String INTERFACES = "Interfaces";
	private InterfacesModel myModel;
	
	
	public TestBatteryInterfacesTreeNode(TestBatteryImpl theBattery) {
//		super(INTERFACES);
		
		myModel = new InterfacesModel(theBattery);
		theBattery.addPropertyChangeListener(TestBatteryImpl.PROP_INTERFACES, this);
		updateChildren();
	}

	private void updateChildren() {
		int index = 0;
		for (AbstractInterface next : myModel.getInterfaces()) {
			if (getChildCount() <= index) {
				InterfaceTreeNode newChild = new InterfaceTreeNode(next);
				add(newChild);
			} else {
				InterfaceTreeNode nextNode = (InterfaceTreeNode) getChildAt(index);
				if (!nextNode.getUserObject().equals(next)) {
					InterfaceTreeNode newChild = new InterfaceTreeNode(next);
					insert(newChild, index);
				}
			}
			index++;
		}
		
		while (getChildCount() > myModel.getInterfaces().size()) {
			remove(getChildCount() - 1);
		}
	}

	public void propertyChange(PropertyChangeEvent theEvt) {
		updateChildren();
	}

}
