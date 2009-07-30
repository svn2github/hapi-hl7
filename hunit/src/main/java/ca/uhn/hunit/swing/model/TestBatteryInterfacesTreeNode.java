package ca.uhn.hunit.swing.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.tree.DefaultMutableTreeNode;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;

public class TestBatteryInterfacesTreeNode extends DefaultMutableTreeNode implements PropertyChangeListener {

	private static final long serialVersionUID = -4977729790093086397L;
	
	public static final String INTERFACES = "Interfaces";
	private InterfacesModel myModel;
	
	
	public TestBatteryInterfacesTreeNode(TestBatteryImpl theBattery) {
		myModel = new InterfacesModel(theBattery);
		theBattery.addPropertyChangeListener(TestBatteryImpl.PROP_INTERFACES, this);
	}

	private void updateChildren() {
		int index = 0;
		for (AbstractInterface next : myModel.getInterfaces()) {
			
		}
	}

	public void propertyChange(PropertyChangeEvent theEvt) {
		updateChildren();
	}

}
