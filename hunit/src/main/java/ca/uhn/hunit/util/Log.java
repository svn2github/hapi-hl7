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
package ca.uhn.hunit.util;

import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.iface.JmsHl7V2InterfaceImpl;
import ca.uhn.hunit.test.ITest;
import ca.uhn.hunit.test.TestBatteryImpl;

public class Log {

	public void info(AbstractInterface theInterface, String theMessage) {
		info("INTERFACE[" + theInterface.getId() + "] " + theMessage);
	}
	
	private void info(String theMessage) {
		System.out.println("[INFO]  " + theMessage);
	}

	private void error(String theMessage) {
		System.out.println("[ERROR] " + theMessage);
	}

   private void warn(String theMessage) {
	        System.out.println("[WARN] " + theMessage);
	    }

	public void info(TestBatteryImpl theTestBatteryImpl,
			String theMessage) {
		info("BATTERY[" + theTestBatteryImpl.getName() + "] " + theMessage);
	}

	public void error(TestBatteryImpl theTestBatteryImpl,
			String theMessage) {
		error("BATTERY[" + theTestBatteryImpl.getName() + "] " + theMessage);
	}

	public void info(ITest theTestImpl, String theMessage) {
		info("TEST[" + theTestImpl.getName() + "] " + theMessage);
	}

	public void error(ITest theTestImpl, String theMessage) {
		error("TEST[" + theTestImpl.getName() + "] " + theMessage);
	}

	public void error(AbstractInterface theInterface, String theMessage) {
		error("INTERFACE[" + theInterface.getId() + "] " + theMessage);
	}

    public void warn(AbstractInterface theInterface, String theMessage) {
        warn("INTERFACE[" + theInterface.getId() + "] " + theMessage);
    }
	
}
