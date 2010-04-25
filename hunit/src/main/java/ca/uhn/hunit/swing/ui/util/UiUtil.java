/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.swing.ui.util;

import java.awt.Color;
import java.beans.PropertyVetoException;
import javax.swing.JTextField;
import org.apache.commons.lang.StringUtils;

/**
 * Swing related classes
 */
public abstract class UiUtil {

    /**
     * @return Returns the background to be used in text fields containing invalid data
     */
    public static java.awt.Color getErrorBackgroundColor() {
        return Color.red;
    }

    /**
     * @return Returns the background to be used in text fields containing valid data
     */
    public static java.awt.Color getOkBackgroundColor() {
        return Color.white;
    }

    /**
     * Retrieves a positive integer value from a text field and returns it. If
     * the value of the text field can not be parsed into a positive integer (0-maxint), the
     * background of the text field is turned red, and an exception is thrown
     */
    public static int getPositiveIntegerFromTextfield(JTextField theTextField) throws BadValueException {
        String text = theTextField.getText();
        String newText = text.replaceAll("[^0-9]", "");
        if (!StringUtils.equals(text, newText)) {
            theTextField.setText(newText);
        }

        if (StringUtils.isBlank(text)) {
            theTextField.setBackground(Color.red);
            throw new BadValueException();
        }

        try {
            final int retVal = Integer.parseInt(newText);
            theTextField.setBackground(Color.white);
            return retVal;
        } catch (NumberFormatException e) {
            theTextField.setBackground(Color.red);
            throw new BadValueException();
        }
    }

    /**
     * Retrieves a String value from a text field and returns it. The string must
     * contain no whitespace, any found is removed. If a valid string is not
     * found, the background of the text field is turned red, and an exception is thrown
     */
    public static String getNonEmptyNoWhitespaceString(JTextField theTextField) throws BadValueException {
        String text = theTextField.getText();
        StringBuilder newTextBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char nextChar = text.charAt(i);
            if (!Character.isWhitespace(nextChar)) {
                newTextBuilder.append(nextChar);
            }
        }
        String newText = newTextBuilder.toString();

        if (!StringUtils.equals(text, newText)) {
            theTextField.setText(newText);
        }

        if (newText.isEmpty()) {
            theTextField.setBackground(Color.red);
            throw new BadValueException();
        } else {
            theTextField.setBackground(Color.white);
            return newText;
        }

    }

    /**
     * Simple exception type for use in property getting methods
     */
    public static class BadValueException extends Exception {
        // nothing
    }
}
