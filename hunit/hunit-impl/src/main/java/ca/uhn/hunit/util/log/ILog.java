/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.util.log;

import org.apache.commons.logging.Log;

/**
 *
 * @author James
 */
public interface ILog extends Log {
    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, Throwable t, EventCodeEnum theEventCode);

    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object theMessage, EventCodeEnum theEventCode);

    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     */
    public void info(Object message, EventCodeEnum theEventCode);
}
