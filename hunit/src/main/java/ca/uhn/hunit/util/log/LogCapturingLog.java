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
package ca.uhn.hunit.util.log;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author James
 */
public class LogCapturingLog implements ILogProvider {

    private List<ILogListener> myListeners = new ArrayList<ILogListener>();

    public Log get(AbstractInterface theInterface) {
        return new MyLog(theInterface);
    }

    public Log get(TestImpl theTest) {
        return new MyLog(theTest);
    }

    public Log get(TestBatteryImpl theTest) {
        return new MyLog(theTest);
    }

    public void registerListener(ILogListener theLogListener) {
        myListeners.add(theLogListener);
    }

    public Log getSystem(Class<?> theClass) {
        return new MyLog(theClass);
    }

    private class MyLog implements Log {

        private final Object mySourceObject;
        private final Log myCommongLoggingLog;

        private MyLog(AbstractInterface theInterface) {
            myCommongLoggingLog = LogFactory.getLog("hunit.interface." + theInterface.getId());
            mySourceObject = theInterface;
        }

        private MyLog(TestImpl theTest) {
            myCommongLoggingLog = LogFactory.getLog("hunit.test." + theTest.getName());
            mySourceObject = theTest;
        }

        private MyLog(TestBatteryImpl theBattery) {
            myCommongLoggingLog = LogFactory.getLog("hunit.battery." + theBattery.getName());
            mySourceObject = theBattery;
        }

        private MyLog(Class<?> theClass) {
            myCommongLoggingLog = LogFactory.getLog("hunit.system." + theClass.getName());
            mySourceObject = theClass;
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
            myCommongLoggingLog.trace(message);
        }

        public void trace(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.TRACE, message, t);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void debug(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.DEBUG, message, null);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void debug(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.DEBUG, message, t);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void info(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.INFO, message, null);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void info(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.INFO, message, t);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void warn(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.WARN, message, null);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void warn(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.WARN, message, t);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void error(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.ERROR, message, null);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void error(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.ERROR, message, t);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void fatal(Object message) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.FATAL, message, null);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        public void fatal(Object message, Throwable t) {
            LogEvent event = new LogEvent(mySourceObject, LogLevel.FATAL, message, null);
            broadcast(event);
            myCommongLoggingLog.trace(message);
        }

        private void broadcast(LogEvent event) {
            for (ILogListener nextLogListener : myListeners) {
                try {
                    nextLogListener.logEvent(event);
                } catch (Exception e) {
                    myCommongLoggingLog.error("Failed to broadcast log event: ", e);
                }
            }
        }
    }
}
