/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.util;

import java.util.Comparator;

/**
 *
 * @author James
 */
public class ClassNameComparator implements Comparator<Class<?>> {
    //~ Methods --------------------------------------------------------------------------------------------------------

    @Override
    public int compare(Class<?> o1, Class<?> o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
