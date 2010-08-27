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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.swing.controller;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.iface.JavaCallableInterfaceImpl;
import ca.uhn.hunit.iface.JmsInterfaceImpl;
import ca.uhn.hunit.iface.MllpHl7V2InterfaceImpl;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.msg.XmlMessageImpl;
import ca.uhn.hunit.swing.controller.ctx.AbstractContextController;
import ca.uhn.hunit.swing.controller.ctx.BatteryEditorContextController;
import ca.uhn.hunit.swing.controller.ctx.BatteryExecutionContextController;
import ca.uhn.hunit.swing.controller.ctx.EventEditorContextController;
import ca.uhn.hunit.swing.controller.ctx.Hl7V2MessageEditorController;
import ca.uhn.hunit.swing.controller.ctx.JavaCallableInterfaceEditorContextController;
import ca.uhn.hunit.swing.controller.ctx.JmsInterfaceContextController;
import ca.uhn.hunit.swing.controller.ctx.MllpHl7v2InterfaceEditorContextController;
import ca.uhn.hunit.swing.controller.ctx.TestEditorController;
import ca.uhn.hunit.swing.controller.ctx.XmlMessageEditorController;
import ca.uhn.hunit.swing.ui.DialogUtil;
import ca.uhn.hunit.swing.ui.SwingRunner;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.log.LogFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;

/**
 * 
 * @author James
 */
public class SwingRunnerController {
	// ~ Instance fields
	// ------------------------------------------------------------------------------------------------

	private AbstractContextController<?> myCtxController;
	private final LogFactory myLog;
	private final SwingRunner myView;
	private TestBatteryImpl myBattery;

	// ~ Constructors
	// ---------------------------------------------------------------------------------------------------

	public SwingRunnerController() {
		setLookAndFeel();

		myLog = new LogFactory();
		myBattery = new TestBatteryImpl();

		myView = new SwingRunner(this, myBattery);
		myView.setVisible(true);

		selectBattery(myBattery);
	}

	public SwingRunnerController(Resource theDefinitionFile) throws Exception {
		setLookAndFeel();

		if (!theDefinitionFile.exists()) {
			throw new IOException();
		}

		myLog = new LogFactory();
		myBattery = new TestBatteryImpl(theDefinitionFile);

		myView = new SwingRunner(this, myBattery);
		myView.setVisible(true);

		selectBattery(myBattery);
	}

	// ~ Methods
	// --------------------------------------------------------------------------------------------------------

	public void addInterfaceMllpHl7v2() {
		myBattery.addEmptyInterfaceMllpHl7V2();
	}

	/**
	 * Adds a new test with no events
	 */
	public void addTestEmpty() {
		myBattery.addEmptyTest();
	}

	private boolean confirmLoseUnsaved() {
		if ((myBattery != null) && !myBattery.isEmpty()) {
			if (DialogUtil.showYesNoDialog(myView, Strings.getMessage("command.new.prompt_to_save"))) {
				if (!save()) {
					return false;
				}
			}
		}

		return true;
	}

	private JFileChooser createFileChooser() {
		JFileChooser chooser = new JFileChooser(".");
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("hUnit Battery", "hunit.xml", "xml"));
		chooser.setAcceptAllFileFilterUsed(true);

		return chooser;
	}

	public void execute() {
		BatteryExecutionContextController ctxController = new BatteryExecutionContextController(myBattery);
		navigateTo(ctxController);
	}

	public LogFactory getLog() {
		return myLog;
	}

	/**
	 * @param args
	 *            the command line arguments
	 * @throws JAXBException
	 * @throws ConfigurationException
	 * @throws InterfaceWontStartException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileSystemResource defFile = new FileSystemResource("src/test/resources/unit_tests_jms.xml");
					new SwingRunnerController(defFile);
				} catch (Exception ex) {
					Logger.getLogger(SwingRunnerController.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
	}

	private void navigateTo(AbstractContextController<?> ctxController) {
		if (myCtxController != null) {
			myCtxController.tearDown();
		}

		myView.setContextController(ctxController);
		myCtxController = ctxController;
	}

	public void newFile() {
		if (!confirmLoseUnsaved()) {
			return;
		}

		disposeExistingBattery();
		myBattery = new TestBatteryImpl();
		myView.setBattery(myBattery);
		selectBattery(myBattery);
	}

	public void newTemplateHl7InAndOut() {
		if (!confirmLoseUnsaved()) {
			return;
		}

		disposeExistingBattery();
		myBattery = new TestBatteryImpl();

		try {
			myBattery.load(new ClassPathResource("/ca/uhn/hunit/templates/hl7_in_and_out.xml"));
		} catch (Exception ex) {
			myLog.getSystem(SwingRunnerController.class).error(ex.getMessage(), ex);
			DialogUtil.showErrorMessage(myView, ex.getMessage());
		}

		myView.setBattery(myBattery);
		selectBattery(myBattery);
	}

	public void open() {
		JFileChooser chooser = createFileChooser();

		int value = chooser.showOpenDialog(myView);

		if (value == JFileChooser.APPROVE_OPTION) {
			FileSystemResource inputFile = new FileSystemResource(chooser.getSelectedFile());

			try {
				TestBatteryImpl newBattery = new TestBatteryImpl(inputFile);
				disposeExistingBattery();
				myBattery = newBattery;
				myView.setBattery(myBattery);

				String message = Strings.getMessage("command.load.success");
				myLog.getSystem(getClass()).info(message);
			} catch (JAXBException ex) {
				String message = Strings.getMessage("command.load.error.jaxb", ex.getMessage());
				myLog.getSystem(getClass()).error(message, ex);
				DialogUtil.showErrorMessage(myView, message);
			} catch (ConfigurationException ex) {
				String message = Strings.getMessage("command.load.error.config", ex.getMessage());
				myLog.getSystem(getClass()).error(message, ex);
				DialogUtil.showErrorMessage(myView, message);
			}
		}
	}

	public boolean save() {
		if (myBattery.getFile() == null) {
			return saveAs();
		}

		try {
			myBattery.save();

			String message = Strings.getMessage("command.save.success");
			myLog.getSystem(getClass()).info(message);

			return true;
		} catch (IOException ex) {
			String message = Strings.getMessage("error.problem_during_save", ex.getMessage());
			myLog.getSystem(getClass()).error(message, ex);
			DialogUtil.showErrorMessage(myView, message);

			return false;
		} catch (JAXBException ex) {
			String message = Strings.getMessage("error.problem_during_save", ex.getMessage());
			myLog.getSystem(getClass()).error(message, ex);
			DialogUtil.showErrorMessage(myView, message);

			return false;
		}
	}

	public boolean saveAs() {
		JFileChooser chooser = createFileChooser();

		int value = chooser.showSaveDialog(myView);

		if (value == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();

			if (selectedFile.getName().indexOf(".") == -1) {
				selectedFile = new File(selectedFile.getAbsolutePath() + ".hunit.xml");
			}

			if (selectedFile.exists()) {
				String message = Strings.getMessage("command.save.confirm_overwrite", selectedFile.getName());

				if (!DialogUtil.showOkCancelDialog(myView, message)) {
					return false;
				}
			}

			myBattery.setFile(selectedFile);

			try {
				myBattery.save();

				String message = Strings.getMessage("command.save.success");
				myLog.getSystem(getClass()).info(message);

				return true;
			} catch (IOException ex) {
				String message = Strings.getMessage("error.problem_during_save", ex.getMessage());
				myLog.getSystem(getClass()).error(message, ex);
				DialogUtil.showErrorMessage(myView, message);

				return false;
			} catch (JAXBException ex) {
				String message = Strings.getMessage("error.problem_during_save", ex.getMessage());
				myLog.getSystem(getClass()).error(message, ex);
				DialogUtil.showErrorMessage(myView, message);

				return false;
			}
		} else {
			return false;
		}
	}

	public void selectBattery(TestBatteryImpl battery) {
		BatteryEditorContextController controller = new BatteryEditorContextController(myBattery);
		navigateTo(controller);
	}

	/**
	 * Set the interface editor to the main context area
	 */
	public void selectInterface(AbstractInterface<?> userObject) {
		AbstractContextController<?> ctxController;

		if (userObject instanceof MllpHl7V2InterfaceImpl) {
			ctxController = new MllpHl7v2InterfaceEditorContextController((MllpHl7V2InterfaceImpl) userObject);
		} else if (userObject instanceof JmsInterfaceImpl) {
			ctxController = new JmsInterfaceContextController((JmsInterfaceImpl) userObject);
		} else if (userObject instanceof JavaCallableInterfaceImpl) {
			ctxController = new JavaCallableInterfaceEditorContextController((JavaCallableInterfaceImpl) userObject);
		} else {
			System.out.println("Unknown interface: " + userObject);

			return;
		}

		navigateTo(ctxController);
	}

	public void selectMessage(AbstractMessage<?> theMessage) {
		AbstractContextController<?> ctxController;

		if (theMessage instanceof Hl7V2MessageImpl) {
			ctxController = new Hl7V2MessageEditorController((Hl7V2MessageImpl) theMessage);
		} else if (theMessage instanceof XmlMessageImpl) {
			ctxController = new XmlMessageEditorController((XmlMessageImpl) theMessage);
		} else {
			System.out.println("Unknown message: " + theMessage);

			return;
		}

		navigateTo(ctxController);
	}

	public void selectTest(TestImpl test) {
		TestEditorController ctxController = new TestEditorController(myBattery, test);
		navigateTo(ctxController);
	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(SwingRunnerController.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(SwingRunnerController.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(SwingRunnerController.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(SwingRunnerController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Cleans up any resources held by the current battery, if any
	 */
	private void disposeExistingBattery() {
		if (myBattery != null) {
			myBattery.dispose();
		}
	}

	/**
	 * Selects an event for editing in the main window
	 * @param theTest 
	 */
	public void selectEvent(TestImpl theTest, AbstractEvent theEvent) {
		EventEditorContextController ctxController;
		try {
			ctxController = new EventEditorContextController(theTest, theEvent);
		} catch (ConfigurationException e) {
			myLog.getSystem(getClass()).error(e.getMessage(), e);
			DialogUtil.showErrorMessage(myView, e.getMessage());
			return;
		}
		navigateTo(ctxController);
	}

    public void addInterfaceJavaCallable() {
		myBattery.addEmptyInterfaceJavaCallable();
    }
}
