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
package ca.uhn.hunit.util.log;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommonsLoggingLog implements ILogProvider {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final Map<String, ILog> ourLogs = Collections.synchronizedMap(new HashMap<String, ILog>());

    //~ Methods --------------------------------------------------------------------------------------------------------

    public ILog get(AbstractInterface theInterface) {
        return getLog("hunit.interface." + theInterface.getId());
    }

    public ILog get(TestImpl theTest) {
        return getLog("hunit.test." + theTest.getName());
    }

    public ILog get(TestBatteryImpl theTest) {
        return getLog("hunit.battery." + theTest.getName());
    }

    private static ILog getLog(String theName) {
        ILog retVal = ourLogs.get(theName);

        if (retVal == null) {
            retVal = new MyLog(theName);
            ourLogs.put(theName, retVal);
        }

        return retVal;
    }

    public ILog getSystem(Class<?> theClass) {
        return getLog("hunit.system." + theClass.getName());
    }

    //~ Inner Classes --------------------------------------------------------------------------------------------------

    private static class MyLog implements ILog {
        private Log myWrappedLog;

        private MyLog(String theLog) {
            myWrappedLog = LogFactory.getLog(theLog);
        }

        public void debug(Object message) {
            myWrappedLog.debug(message);
        }

        public void debug(Object message, Throwable t) {
            myWrappedLog.debug(message, t);
        }

        public void error(Object message, Throwable t, EventCodeEnum theEventCode) {
            myWrappedLog.error(message, t);
        }

        public void error(Object message) {
            myWrappedLog.error(message);
        }

        public void error(Object message, Throwable t) {
            myWrappedLog.error(message, t);
        }

        public void error(Object theMessage, EventCodeEnum theEventCode) {
            myWrappedLog.error(theMessage);
        }

        public void fatal(Object message) {
            myWrappedLog.fatal(message);
        }

        public void fatal(Object message, Throwable t) {
            myWrappedLog.fatal(message, t);
        }

        public void info(Object message, EventCodeEnum theEventCode) {
            myWrappedLog.info(message);
        }

        public void info(Object message) {
            myWrappedLog.info(message);
        }

        public void info(Object message, Throwable t) {
            myWrappedLog.info(message, t);
        }

        public boolean isDebugEnabled() {
            return myWrappedLog.isDebugEnabled();
        }

        public boolean isErrorEnabled() {
            return myWrappedLog.isErrorEnabled();
        }

        public boolean isFatalEnabled() {
            return myWrappedLog.isFatalEnabled();
        }

        public boolean isInfoEnabled() {
            return myWrappedLog.isInfoEnabled();
        }

        public boolean isTraceEnabled() {
            return myWrappedLog.isTraceEnabled();
        }

        public boolean isWarnEnabled() {
            return myWrappedLog.isWarnEnabled();
        }

        public void trace(Object message) {
            myWrappedLog.trace(message);
        }

        public void trace(Object message, Throwable t) {
            myWrappedLog.trace(message, t);
        }

        public void warn(Object message) {
            myWrappedLog.warn(message);
        }

        public void warn(Object message, Throwable t) {
            myWrappedLog.warn(message, t);
        }
    }
}
