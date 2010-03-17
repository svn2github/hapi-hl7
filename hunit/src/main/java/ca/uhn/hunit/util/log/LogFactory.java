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
package ca.uhn.hunit.util.log;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author James
 */
public class LogFactory {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

	/** Singleton instance */
	public static final LogFactory INSTANCE = new LogFactory();
	
    private static final CommonsLoggingLog ourWrappedLog = new CommonsLoggingLog();

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private List<ILogListener> myListeners = new ArrayList<ILogListener>();

    //~ Methods --------------------------------------------------------------------------------------------------------

    public ILog get(AbstractInterface theInterface) {
        return new MyLog(theInterface);
    }

    public ILog get(TestImpl theTest) {
        return new MyLog(theTest);
    }

    public ILog get(TestBatteryImpl theTest) {
        return new MyLog(theTest);
    }

    public ILog getSystem(Class<?> theClass) {
        return new MyLog(theClass);
    }

    public void registerListener(ILogListener theLogListener) {
        myListeners.add(theLogListener);
    }

    public void unregisterListener(ILogListener theLogListener) {
        myListeners.remove(theLogListener);
    }
    
    //~ Inner Classes --------------------------------------------------------------------------------------------------

    private class MyLog implements ILog {
        private final ILog myWrappedLog;
        private final Object mySourceObject;

        private MyLog(AbstractInterface<?> theInterface) {
            myWrappedLog = ourWrappedLog.get(theInterface);
            mySourceObject = theInterface;
        }

        private MyLog(TestImpl theTest) {
            myWrappedLog = ourWrappedLog.get(theTest);
            mySourceObject = theTest;
        }

        private MyLog(TestBatteryImpl theBattery) {
            myWrappedLog = ourWrappedLog.get(theBattery);
            mySourceObject = theBattery;
        }

        private MyLog(Class<?> theClass) {
            myWrappedLog = ourWrappedLog.getSystem(theClass);
            mySourceObject = theClass;
        }

        private void broadcast(LogEvent event) {
            for (ILogListener nextLogListener : myListeners) {
                try {
                    nextLogListener.logEvent(event);
                } catch (Exception e) {
                    myWrappedLog.error("Failed to broadcast log event: ", e);
                }
            }
        }

        public void debug(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.DEBUG, message, null);
            broadcast(event);
            myWrappedLog.trace(message);
        }

        public void debug(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.DEBUG, message, t);
            broadcast(event);
            myWrappedLog.trace(message,t);
        }

        public void error(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.ERROR, message, null);
            broadcast(event);
            myWrappedLog.error(message);
        }

        public void error(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.ERROR, message, t);
            broadcast(event);
            myWrappedLog.error(message,t);
        }

        public void error(Object message, Throwable t, EventCodeEnum theEventCode) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.ERROR, message, t, theEventCode);
            broadcast(event);
            myWrappedLog.error(message,t);
        }

        public void error(Object message, EventCodeEnum theEventCode) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.ERROR, message, null, theEventCode);
            broadcast(event);
            myWrappedLog.error(message);
        }

        public void fatal(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.FATAL, message, null);
            broadcast(event);
            myWrappedLog.fatal(message);
        }

        public void fatal(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.FATAL, message, null);
            broadcast(event);
            myWrappedLog.fatal(message,t);
        }

        public void info(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.INFO, message, null);
            broadcast(event);
            myWrappedLog.info(message);
        }

        public void info(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.INFO, message, t);
            broadcast(event);
            myWrappedLog.info(message, t);
        }

        public void info(Object message, EventCodeEnum theEventCode) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.INFO, message, null, theEventCode);
            broadcast(event);
            myWrappedLog.info(message);
        }

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
            LogEvent event = new LogEvent(mySourceObject, LogLevel.TRACE, message, null);
            broadcast(event);
            myWrappedLog.trace(message);
        }

        public void trace(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.TRACE, message, t);
            broadcast(event);
            myWrappedLog.trace(message, t);
        }

        public void warn(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.WARN, message, null);
            broadcast(event);
            myWrappedLog.warn(message);
        }

        public void warn(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.WARN, message, t);
            broadcast(event);
            myWrappedLog.warn(message, t);
        }
    }
}
