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
package ca.uhn.hunit.swing.controller.ctx;

import ca.uhn.hunit.iface.JmsInterfaceImpl;
import ca.uhn.hunit.swing.ui.iface.JmsInterfaceEditorForm;
import ca.uhn.hunit.swing.ui.util.IBeanDefinitionController;
import ca.uhn.hunit.util.TypedValueListTableModel;
import ca.uhn.hunit.util.log.ILogProvider;

import java.beans.PropertyVetoException;

import javax.jms.ConnectionFactory;

/**
 *
 * @author James
 */
public class JmsInterfaceContextController extends AbstractInterfaceEditorContextController<JmsInterfaceImpl, JmsInterfaceEditorForm> {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private final JmsInterfaceEditorForm myView;
    private final JmsInterfaceImpl myModel;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public JmsInterfaceContextController(JmsInterfaceImpl theModel) {
        super(theModel);
        myModel = theModel;
        myView = new JmsInterfaceEditorForm();
        myView.setController(this);
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public IBeanDefinitionController getConstructorArgsController() {
        return new MyConstructorArgsController();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JmsInterfaceImpl getModel() {
        return myModel;
    }

    @Override
    public JmsInterfaceEditorForm getView() {
        return myView;
    }

    //~ Inner Classes --------------------------------------------------------------------------------------------------

    private class MyConstructorArgsController implements IBeanDefinitionController {
        @Override
        public TypedValueListTableModel getConstructorArgsTableModel() {
            return myModel.getConstructorArgsTableModel();
        }

        @Override
        public Class<?> getInitialClass() {
            return myModel.getConnectionFactoryClass();
        }

        @Override
        public Class<?> getRequiredSuperclass() {
            return ConnectionFactory.class;
        }

        @Override
        public void setSelectedClass(Class<?> theClass)
                              throws PropertyVetoException {
            myModel.setConnectionFactoryClass(theClass);
        }
    }
}
