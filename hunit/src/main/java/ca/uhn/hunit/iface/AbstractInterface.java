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
package ca.uhn.hunit.iface;

import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.Interface;

public abstract class AbstractInterface implements Comparable<AbstractInterface> {

	private Interface myConfig;
	private String myId;
	private Boolean myAutostart;

	public String getId() {
		return myId;
	}

	public AbstractInterface(Interface theConfig) {
		myConfig = theConfig;
		myId = theConfig.getId();
		myAutostart = theConfig.isAutostart();
		if (myAutostart == null) {
			myAutostart = true;
		}
	}

	public Interface getConfig() {
		return myConfig;
	}

	/**
	 * Subclasses are expected to implement this method and provide their configuration
	 */
	public abstract Interface exportConfig();
	
	/**
	 * Subclasses should make use of this method to export AbstractInterface properties into
	 * the return value for {@link #exportConfig()}  
	 */
	protected void exportConfig(Interface theConfig) {
		theConfig.setAutostart(myAutostart);
		theConfig.setId(myId);
	}
	
	public abstract void start(ExecutionContext theCtx) throws InterfaceWontStartException;
	
	public abstract void stop(ExecutionContext theCtx) throws InterfaceWontStopException;
	
	public abstract TestMessage<?> receiveMessage(TestImpl theTest, ExecutionContext theCtx, long theTimeout) throws TestFailureException;
	
	public abstract void sendMessage(TestImpl theTest, ExecutionContext theCtx, TestMessage<?> theMessage) throws TestFailureException;

	public abstract boolean isStarted();
	
	public boolean isAutostart() {
		return myAutostart;
	}
	
	public int compareTo(AbstractInterface theO) {
		return myId.compareTo(theO.myId);
	}

}
