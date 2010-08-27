/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.swing.ui.util;

import ca.uhn.hunit.util.TypedValueListTableModel;

import java.beans.PropertyVetoException;

/**
 * Controller for {@link BeanDefinitionForm}
 */
public interface IBeanDefinitionController{
    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * Returns the table model containing the constructor args for the bean
     */
    TypedValueListTableModel getConstructorArgsTableModel(  );

    /**
     * Returns the class to populate the UI with
     */
    Class<?> getInitialClass(  );

    /**
     * Returns the superclass or interface which the chosen bean must implement
     */
    Class<?> getRequiredSuperclass(  );

    /**
     * Sets the bean class
     */
    void setSelectedClass( Class<?> theClass ) throws PropertyVetoException;
}
