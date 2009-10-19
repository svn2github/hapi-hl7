/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.util.log;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import java.util.List;
import org.apache.commons.logging.Log;

/**
 *
 * @author James
 */
public class LogCapturingLog implements ILogProvider {

    public Log get(AbstractInterface theInterface) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Log get(TestImpl theTest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Log get(TestBatteryImpl theTest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private class MyLog implements Log
    {

        public boolean isDebugEnabled() {
            return true;
        }

        public boolean isErrorEnabled() {
            return true;
        }

        public boolean isFatalEnabled() {
            return true;
        }

        public boolean isInfoEnabled() {
            return true;
        }

        public boolean isTraceEnabled() {
            return true;
        }

        public boolean isWarnEnabled() {
            return true;
        }

        public void trace(Object message) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void trace(Object message, Throwable t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void debug(Object message) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void debug(Object message, Throwable t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void info(Object message) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void info(Object message, Throwable t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void warn(Object message) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void warn(Object message, Throwable t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void error(Object message) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void error(Object message, Throwable t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void fatal(Object message) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void fatal(Object message, Throwable t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
