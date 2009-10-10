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
package ca.uhn.hunit.util;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.xsd.JavaArgument;
import ca.uhn.hunit.xsd.NamedJavaArgument;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author James
 */
public class TypedValueListTableModel extends AbstractTableModel {

    private final boolean myNamed;
    private final ArrayList<String> myNames = new ArrayList<String>();
    private final ArrayList<Object> myArgs = new ArrayList<Object>();
    private final ArrayList<Class<?>> myArgTypes = new ArrayList<Class<?>>();

    public TypedValueListTableModel(boolean theNamed) {
        myNamed = theNamed;
    }

    public void setValues(List<? extends JavaArgument> theArgDefinitions) throws ConfigurationException {
        for (JavaArgument next : theArgDefinitions) {
            if ("java.lang.String".equals(next.getType())) {
                myArgTypes.add(String.class);
                myArgs.add(next.getValue());
            } else if ("java.lang.Integer".equals(next.getType())) {
                myArgTypes.add(Integer.class);
                myArgs.add(Integer.parseInt(next.getValue()));
            } else if ("int".equals(next.getType())) {
                myArgTypes.add(int.class);
                myArgs.add(Integer.parseInt(next.getValue()));
            } else {
                throw new ConfigurationException("Unknown arg type: " + next.getType());
            }

            if (myNamed) {
                myNames.add(((NamedJavaArgument) next).getName());
            }
        }

    }

    public int getColumnCount() {
        return myNamed ? 3 : 2;
    }

    public int getRowCount() {
        return myArgTypes.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (!myNamed) {
            columnIndex++;
        }

        switch (columnIndex) {
            case 0:
                return myNames.get(rowIndex);
            case 1:
                return myArgs.get(rowIndex);
            case 2:
                return myArgTypes.get(rowIndex);
            default:
                throw new IllegalStateException("Col " + columnIndex);
        }
    }


    public Object getArg(int theIndex) {
        return myArgs.get(theIndex);
    }

    public Class<?> getArgType(int theIndex) {
        return myArgTypes.get(theIndex);
    }

    public String getName(int theIndex) {
        return myNames.get(theIndex);
    }

    public Class<?>[] getArgTypeArray() {
        return myArgTypes.toArray(new Class<?>[myArgTypes.size()]);
    }

    public Object[] getArgArray() {
        return myArgs.toArray(new Object[myArgs.size()]);
    }

}
