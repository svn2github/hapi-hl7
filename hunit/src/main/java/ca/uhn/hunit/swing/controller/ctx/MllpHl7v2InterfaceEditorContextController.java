/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.swing.controller.ctx;

import ca.uhn.hunit.iface.MllpHl7V2InterfaceImpl;
import ca.uhn.hunit.swing.ui.MllpHl7v2InterfaceEditorForm;

/**
 *
 * @author James
 */
public class MllpHl7v2InterfaceEditorContextController extends AbstractInterfaceEditorContextController<MllpHl7V2InterfaceImpl, MllpHl7v2InterfaceEditorForm> {
    private final MllpHl7v2InterfaceEditorForm myView;

    /**
     * Constructor
     */
    public MllpHl7v2InterfaceEditorContextController(MllpHl7V2InterfaceImpl theModel) {
        super(theModel);

        myView = new MllpHl7v2InterfaceEditorForm(this);
    }

    @Override
    public MllpHl7v2InterfaceEditorForm getView() {
        return myView;
    }

}
