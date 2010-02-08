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
package ca.uhn.hunit.swing.model;

import ca.uhn.hunit.util.log.ILogListener;
import ca.uhn.hunit.util.log.LogFactory;
import ca.uhn.hunit.util.log.LogEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 * Table model for displaying event logs
 */
public class LogTableModel extends AbstractTableModel implements ILogListener {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    public static final int COLUMN_LEVEL = 1;
    public static final int COLUMN_MESSAGE = 3;
    public static final int COLUMN_SOURCE = 2;
    public static final int COLUMN_TIME = 0;
    private static final long serialVersionUID = 1L;

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private List<LogEvent> myEvents = new ArrayList<LogEvent>();

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public LogTableModel() {
    	LogFactory.INSTANCE.registerListener(this);
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc }
     */
    public int getColumnCount() {
        return 4;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COLUMN_TIME:
                return "Time";

            case COLUMN_LEVEL:
                return "Level";

            case COLUMN_SOURCE:
                return "Source";

            case COLUMN_MESSAGE:
                return "Message";

            default:
                throw new IllegalArgumentException("" + column);
        }
    }

    /**
     * {@inheritDoc }
     */
    public int getRowCount() {
        return myEvents.size();
    }

    /**
     * {@inheritDoc }
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogEvent event = myEvents.get(rowIndex);

        switch (columnIndex) {
            case COLUMN_TIME:
                return event.getEventTime();

            case COLUMN_LEVEL:
                return event.getLogLevel();

            case COLUMN_SOURCE:
                return event.getModelObject();

            case COLUMN_MESSAGE:
                return event;

            default:
                throw new IllegalArgumentException("" + columnIndex);
        }
    }

    /**
     * {@inheritDoc }
     */
    public void logEvent(LogEvent theLogEvent) {
        myEvents.add(theLogEvent);
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireTableRowsInserted(myEvents.size() - 1, myEvents.size() - 1);
                }
            });
    }

    /**
     * Stop following the log
     */
	public void stopFollowing() {
		LogFactory.INSTANCE.unregisterListener(this);
	}
}
