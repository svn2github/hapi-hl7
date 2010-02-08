/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.swing.controller.ctx;

import ca.uhn.hunit.swing.ui.BatteryEditorForm;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.util.log.ILogProvider;

/**
 *
 * @author James
 */
public class BatteryEditorContextController extends AbstractContextController<BatteryEditorForm> {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private final BatteryEditorForm myView;
    private final TestBatteryImpl myBattery;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public BatteryEditorContextController(TestBatteryImpl theBattery) {
        myBattery = theBattery;
        myView = new BatteryEditorForm();
        myView.setController(this);
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * Returns the battery model for this controller
     */
    public TestBatteryImpl getBattery() {
        return myBattery;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BatteryEditorForm getView() {
        return myView;
    }
}
