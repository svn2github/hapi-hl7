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
package ca.uhn.hunit.test;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;

import ca.uhn.hunit.event.expect.AbstractExpectMessage;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.xsd.TerserMessageRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class TerserRuleImpl {
    //~ Instance fields ------------------------------------------------------------------------------------------------

    private AbstractExpectMessage myExpect;
    private List<Pattern> myNotPatterns = new ArrayList<Pattern>();
    private List<Pattern> myPatterns = new ArrayList<Pattern>();
    private Set<String> myNotValues = new HashSet<String>();
    private Set<String> myValues = new HashSet<String>();
    private String myPath;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public TerserRuleImpl(AbstractExpectMessage theExpect, TerserMessageRule theConfig) {
        this(theExpect);

        myPath = theConfig.getPath();
        myValues.addAll(theConfig.getValue());
        myNotValues.addAll(theConfig.getNotValue());

        for (String next : theConfig.getPattern()) {
            myPatterns.add(Pattern.compile(next));
        }

        for (String next : theConfig.getNotPattern()) {
            myNotPatterns.add(Pattern.compile(next));
        }
    }

    private TerserRuleImpl(AbstractExpectMessage theExpect) {
        myExpect = theExpect;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public TerserMessageRule exportConfig() {
        TerserMessageRule retVal = new TerserMessageRule();

        for (Pattern next : myNotPatterns) {
            retVal.getNotPattern().add(next.pattern());
        }

        for (Pattern next : myPatterns) {
            retVal.getPattern().add(next.pattern());
        }

        retVal.getNotValue().addAll(myNotValues);
        retVal.getValue().addAll(myValues);

        return retVal;
    }

    public static TerserRuleImpl getNotValuesInstance(AbstractExpectMessage theExpect, String thePath,
                                                      String... theValues) {
        TerserRuleImpl retVal = new TerserRuleImpl(theExpect);
        retVal.myPath = thePath;

        for (String string : theValues) {
            retVal.myNotValues.add(string);
        }

        return retVal;
    }

    public static TerserRuleImpl getValuesInstance(AbstractExpectMessage theExpect, String thePath, String... theValues) {
        TerserRuleImpl retVal = new TerserRuleImpl(theExpect);
        retVal.myPath = thePath;

        for (String string : theValues) {
            retVal.myValues.add(string);
        }

        return retVal;
    }

    public void validate(TestMessage theMessage) throws IncorrectMessageReceivedException {
        String value;

        try {
            value = new Terser((Message) theMessage.getParsedMessage()).get(myPath);
        } catch (HL7Exception e) {
            throw new IncorrectMessageReceivedException(myExpect.getTest(),
                                                        (TestMessage) null,
                                                        theMessage,
                                                        e.getMessage());
        }

        if (! myValues.isEmpty()) {
            if (! myValues.contains(value)) {
                throw new IncorrectMessageReceivedException(myExpect.getTest(),
                                                            (TestMessage) null,
                                                            theMessage,
                                                            "Incorrect value for SPEC[" + myPath + "]. Expected[" +
                                                            myValues + "] but found[" + value + "]");
            }
        }

        if (! myNotValues.isEmpty()) {
            if (myNotValues.contains(value)) {
                throw new IncorrectMessageReceivedException(myExpect.getTest(),
                                                            (TestMessage) null,
                                                            theMessage,
                                                            "Incorrect value for SPEC[" + myPath +
                                                            "]. Should not contain[" + myNotValues + "] but found[" +
                                                            value + "]");
            }
        }

        for (Pattern next : myPatterns) {
            if (! next.matcher(value).matches()) {
                throw new IncorrectMessageReceivedException(myExpect.getTest(),
                                                            (TestMessage) null,
                                                            theMessage,
                                                            "Incorrect value for SPEC[" + myPath +
                                                            "]. Expected Pattern[" + next.pattern() + "] but found[" +
                                                            value + "]");
            }
        }

        for (Pattern next : myNotPatterns) {
            if (next.matcher(value).matches()) {
                throw new IncorrectMessageReceivedException(myExpect.getTest(),
                                                            (TestMessage) null,
                                                            theMessage,
                                                            "Incorrect value for SPEC[" + myPath +
                                                            "]. Expected Not Pattern[" + myValues + "] but found[" +
                                                            value + "]");
            }
        }
    }
}
