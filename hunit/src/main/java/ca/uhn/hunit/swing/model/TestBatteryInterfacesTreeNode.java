package ca.uhn.hunit.swing.model;

import javax.swing.tree.DefaultMutableTreeNode;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;

public class TestBatteryInterfacesTreeNode extends DefaultMutableTreeNode {

	private TestBatteryImpl myBattery;

	public TestBatteryInterfacesTreeNode(TestBatteryImpl theBattery) {
		super(theBattery);
		myBattery = theBattery;
		
		updateChildren();
	}

	private void updateChildren() {
		for (AbstractInterface next : myBattery.getInterfaces()) {
			
		}
	}

}
