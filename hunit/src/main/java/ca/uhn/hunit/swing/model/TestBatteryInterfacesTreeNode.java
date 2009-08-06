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
