/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.swing.model;

import javax.swing.tree.DefaultMutableTreeNode;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;

/**
 *
 * @author James
 */
public class InterfaceTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1949757870372912053L;

	public InterfaceTreeNode(AbstractInterface theInterface) {
		super(theInterface, false);
	}

}
