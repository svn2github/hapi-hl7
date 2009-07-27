package ca.uhn.hunit.test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractPropertyChangeSupport {

	private PropertyChangeSupport myPropertyChangeSupport = new PropertyChangeSupport(this);

	public AbstractPropertyChangeSupport() {
		super();
	}

	public void addPropertyChangeListener(String thePropertyName, PropertyChangeListener theListener) {
		myPropertyChangeSupport.addPropertyChangeListener(thePropertyName, theListener);
	}

	protected void firePropertyChange(String thePropertyName, Object theOldValue, Object theNewValue) {
		myPropertyChangeSupport.firePropertyChange(thePropertyName, theOldValue, theNewValue);
	}

}