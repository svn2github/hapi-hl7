/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.swing.controller.ctx;

import ca.uhn.hunit.swing.ui.SwingRunner;
import ca.uhn.hunit.test.TestBatteryImpl;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 *
 * @author James
 */
public class SwingRunnerController {
    private final SwingRunner myView;

    public SwingRunnerController() throws Exception {

        File defFile = new File("src/test/resources/unit_tests_hl7.xml");
        if (!defFile.exists()) {
            throw new IOException();
        }
		final TestBatteryImpl batteryImpl = new TestBatteryImpl(defFile);
        myView = new SwingRunner(this, batteryImpl);

        myView.setVisible(true);
    }



        /**
    * @param args the command line arguments
     * @throws JAXBException
     * @throws ConfigurationException
     * @throws InterfaceWontStartException
     * @throws URISyntaxException
    */
    public static void main(String args[]) throws Exception {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());

            java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new SwingRunnerController();
                } catch (Exception ex) {
                    Logger.getLogger(SwingRunnerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }


}
