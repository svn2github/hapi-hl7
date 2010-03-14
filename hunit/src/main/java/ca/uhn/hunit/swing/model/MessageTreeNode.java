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
package ca.uhn.hunit.swing.model;

import ca.uhn.hunit.msg.AbstractMessage;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author James
 */
public class MessageTreeNode extends DefaultMutableTreeNode {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final long serialVersionUID = 1949757870372912053L;

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private final AbstractMessage<?> myMessage;
    private final String myName;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public MessageTreeNode(String theName, AbstractMessage<?> theMessage) {
        super(theMessage, false);
        myName = theName;
        myMessage = theMessage;
    }

    /**
     * Returns the name associated with this message (as defined by the event that contains it)
     */
    public String getName() {
        return myName;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public AbstractMessage<?> getMessage() {
        return myMessage;
    }
}
