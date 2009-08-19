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
 * Created on Jul 30, 2009
 */
package ca.uhn.hunit.iface;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * Wraps a JMS connection factory to provide usernames and passwords
 * 
 * @author <a href="mailto:james.agnew@uhn.on.ca">James Agnew</a>
 * @version $Revision: 1.2 $ updated on $Date: 2009-08-19 01:55:33 $ by $Author: jamesagnew $
 */
public class JmsConnectionFactory implements ConnectionFactory
{

    private ConnectionFactory myConnectionFactory;
    private String myUsername;
    private String myPassword;


    /**
     * @param theConnectionFactory
     * @param theUsername
     * @param thePassword
     */
    public JmsConnectionFactory(ConnectionFactory theConnectionFactory, String theUsername, String thePassword) {
        myConnectionFactory = theConnectionFactory;
        myUsername = theUsername;
        myPassword = thePassword;
    }


    /**
     * {@inheritDoc}
     */
    public Connection createConnection() throws JMSException {
        if (myUsername != null) {
            return myConnectionFactory.createConnection(myUsername, myPassword);
        } else {
            return myConnectionFactory.createConnection();
        }
    }


    /**
     * {@inheritDoc}
     */
    public Connection createConnection(String theArg0, String theArg1) throws JMSException {
        return myConnectionFactory.createConnection(theArg0, theArg1);
    }

}
