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

package ca.uhn.hunit.swing.ui.run;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.swing.model.LogTableModel;
import ca.uhn.hunit.swing.ui.ImageFactory;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.log.LogEvent;
import ca.uhn.hunit.util.log.LogLevel;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author James
 */
public class LogTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat ourTimeFormat = new SimpleDateFormat("HH:mm:ss,SSS");
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setForeground(Color.black);
        setIcon(null);
        setHorizontalTextPosition(SwingConstants.RIGHT);

        if (column == LogTableModel.COLUMN_MESSAGE) {
            LogEvent event = (LogEvent) value;
            setText(event.getMessage());
            if (event.getEventCode() != null) {
                switch (event.getEventCode()) {
                    case TEST_FAILED:
                        setIcon(ImageFactory.getTestFailed());
                        break;
                    case TEST_PASSED:
                        setIcon(ImageFactory.getTestPassed());
                        break;
                    case TEST_STARTED:
                        setIcon(ImageFactory.getTestRunning());
                        break;
                }
            }

        } else if (value instanceof String) {
            setText((String)value);
        } else if (value instanceof Date) {
            setText(ourTimeFormat.format((Date)value));
        } else if (value instanceof LogLevel) {
            final LogLevel logLevel = (LogLevel) value;
            if (logLevel.ordinal() >= LogLevel.WARN.ordinal()) {
                setForeground(Color.red);
            }
            setText((logLevel).name());
        } else if (value instanceof TestBatteryImpl) {
            setText("Battery[" + ((TestBatteryImpl)value).getName() + "]");
        } else if (value instanceof TestImpl) {
            setText("Test[" + ((TestImpl)value).getName() + "]");
        } else if (value instanceof AbstractInterface) {
            setText("Interface[" + ((AbstractInterface)value).getId() + "]");
        } else {
            setText("System");
        }

        setToolTipText(getText());

        return this;
    }


}
