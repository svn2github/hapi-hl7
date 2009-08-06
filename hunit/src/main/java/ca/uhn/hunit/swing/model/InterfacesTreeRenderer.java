package ca.uhn.hunit.swing.model;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;

public class InterfacesTreeRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree theTree, Object theValue, boolean theSelected, boolean theExpanded, boolean theLeaf, int theRow, boolean theHasFocus) {
		super.getTreeCellRendererComponent(theTree, theValue, theSelected, theExpanded, theLeaf, theRow, theHasFocus);
		
		if (theValue instanceof TestBatteryTreeNode) {
			TestBatteryTreeNode node = (TestBatteryTreeNode)theValue;
			TestBatteryImpl battery = (TestBatteryImpl)node.getUserObject();
			setText(battery.getName());
		} else if (theValue instanceof TestBatteryInterfacesTreeNode) {
			setText("Interfaces");
		} else if (theValue instanceof InterfaceTreeNode) {
			InterfaceTreeNode node = (InterfaceTreeNode)theValue;
			AbstractInterface ai = (AbstractInterface)node.getUserObject();
			String name = (String)ai.getId();
			setText(name);
		} else {
			setText("--" + theValue.toString());
		}
		
		return this;
	}

}
