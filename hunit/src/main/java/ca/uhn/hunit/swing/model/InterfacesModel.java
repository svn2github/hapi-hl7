package ca.uhn.hunit.swing.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;

public class InterfacesModel implements PropertyChangeListener {

	private TestBatteryImpl myBattery;
	private List<AbstractInterface> myInterfaces;

	public TestBatteryImpl getBattery() {
		return myBattery;
	}

	public List<AbstractInterface> getInterfaces() {
		return myInterfaces;
	}

	public InterfacesModel(TestBatteryImpl theBattery) {
		myBattery = theBattery;
		updateInterfaces();
	}

	private void updateInterfaces() {
		List<AbstractInterface> oldValue = myBattery.getInterfaces();
		myInterfaces = oldValue;
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent theEvt) {
		updateInterfaces();
	}
	
}
