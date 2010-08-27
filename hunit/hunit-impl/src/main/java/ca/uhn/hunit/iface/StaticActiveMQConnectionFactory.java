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
 * Created on Aug 17, 2009
 */
package ca.uhn.hunit.iface;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * ConnectionFactory which always returns a shared static instance of an ActiveMQ
 * connection factory. This is not intended to be a production class, just
 * for unit test and demonstration purposes.
 */
public class StaticActiveMQConnectionFactory implements ConnectionFactory {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static ActiveMQConnectionFactory ourConnectionFactory;

    static {
        ourConnectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }

    //~ Constructors ---------------------------------------------------------------------------------------------------

    /**
     * Constructor
     */
    public StaticActiveMQConnectionFactory() {
        // nothing
    }

    /**
     * Constructor
     *
     * @param theString NOT USED, just for demo purposes
     * @param theInteger NOT USED, just for demo purposes
     */
    public StaticActiveMQConnectionFactory(String theString, Integer theInteger) {
        // nothing
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection createConnection() throws JMSException {
        return ourConnectionFactory.createConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection createConnection(String theArg0, String theArg1)
                                throws JMSException {
        return createConnection();
    }

    public static void reset() {
        ourConnectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }
}
