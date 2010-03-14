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
package ca.uhn.hunit.event.expect;

import ca.uhn.hl7v2.model.Message;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.test.*;
import ca.uhn.hunit.xsd.ExpectMessageAny;
import ca.uhn.hunit.xsd.Hl7V2ExpectAck;
import ca.uhn.hunit.xsd.Hl7V2ExpectNak;
import ca.uhn.hunit.xsd.Hl7V2ExpectRules;
import ca.uhn.hunit.xsd.TerserMessageRule;

import java.util.ArrayList;
import java.util.List;

public class Hl7V2ExpectRulesImpl extends AbstractHl7V2ExpectMessage {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private List<TerserRuleImpl> myRules = new ArrayList<TerserRuleImpl>();
    private boolean myExpectAck = false;
    private boolean myExpectNak = false;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public Hl7V2ExpectRulesImpl(TestImpl theTest, Hl7V2ExpectRules theConfig)
                         throws ConfigurationException {
        super(theTest, theConfig);

        for (TerserMessageRule next : theConfig.getRule()) {
            addRule(new TerserRuleImpl(this, next));
        }
    }

    public Hl7V2ExpectRulesImpl(TestImpl theTest, Hl7V2ExpectAck theConfig)
                         throws ConfigurationException {
        this(theTest, (Hl7V2ExpectRules) theConfig);

        myExpectAck = true;
        addRule(TerserRuleImpl.getValuesInstance(this, "/MSA-1", "AA"));
    }

    public Hl7V2ExpectRulesImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectNak theConfig)
                         throws ConfigurationException {
        this(theTest, (Hl7V2ExpectRules) theConfig);

        myExpectNak = false;
        addRule(TerserRuleImpl.getNotValuesInstance(this, "/MSA-1", "AA"));
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    protected void addRule(TerserRuleImpl theTerserRuleImpl) {
        myRules.add(theTerserRuleImpl);
    }

    public Hl7V2ExpectRules exportConfig(Hl7V2ExpectRules theConfig) {
        super.exportConfig(theConfig);

        for (TerserRuleImpl next : myRules) {
            theConfig.getRule().add(next.exportConfig());
        }

        return theConfig;
    }

    @Override
    public Hl7V2ExpectRules exportConfigToXml() {
        Hl7V2ExpectRules retVal = new Hl7V2ExpectRules();

//        ExpectMessageAny expectMessage = new ExpectMessageAny();
//        if (myExpectAck) {
//            retVal = new Hl7V2ExpectAck();
//            expectMessage.setHl7V2Ack((Hl7V2ExpectAck) retVal);
//        } else if (myExpectNak) {
//            retVal = new Hl7V2ExpectNak();
//            expectMessage.setHl7V2Nak((Hl7V2ExpectNak) retVal);
//        } else {
//            expectMessage.setHl7V2Rules(retVal);
//        }
        retVal = exportConfig(retVal);

        return retVal;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ExpectMessageAny exportConfigToXmlAndEncapsulate() {
        ExpectMessageAny retVal = new ExpectMessageAny();
        retVal.setHl7V2Rules(exportConfigToXml());

        return retVal;
    }

    @Override
    public void validateMessage(TestMessage<Message> theMessage)
                         throws IncorrectMessageReceivedException {
        for (TerserRuleImpl next : myRules) {
            next.validate(theMessage);
        }
    }


}
