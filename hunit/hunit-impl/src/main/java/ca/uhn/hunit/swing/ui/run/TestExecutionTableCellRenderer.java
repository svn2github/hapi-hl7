/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
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
package ca.uhn.hunit.swing.ui.run;

import ca.uhn.hunit.run.ExecutionStatusEnum;
import ca.uhn.hunit.swing.ui.ImageFactory;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author James
 */
public class TestExecutionTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            setText("");
            setIcon(null);
        } else if (value instanceof TestBatteryImpl) {
            TestBatteryImpl battery = (TestBatteryImpl) value;
            setText(battery.getName());
            setIcon(null);
        } else if (value instanceof TestImpl) {
            TestImpl test = (TestImpl) value;
            setText(test.getName());
            setIcon(null);
        } else if (value instanceof ExecutionStatusEnum) {
            ExecutionStatusEnum status = (ExecutionStatusEnum) value;
            String string = ResourceBundle.getBundle("ca/uhn/hunit/l10n/UiStrings").getString("execution.table.status." + status.name());
            setText(string);
            switch (status) {
                case FAILED:
                    setIcon(ImageFactory.getTestFailed());
                    break;
                case NOT_YET_STARTED:
                    setIcon(null);
                    break;
                case PASSED:
                    setIcon(ImageFactory.getTestPassed());
                    break;
                case RUNNING:
                    setIcon(ImageFactory.getTestRunning());
                    break;
            }
        } else {
            setText("");
            setIcon(null);
        }

        return this;
    }
}
