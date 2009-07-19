package ca.uhn.hunit.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.xsd.TerserMessageRule;

public class TerserRuleImpl {

	private String myPath;
	private List<Pattern> myPatterns = new ArrayList<Pattern>();
	private Set<String> myValues = new HashSet<String>();
	private List<Pattern> myNotPatterns  = new ArrayList<Pattern>();
	private Set<String> myNotValues = new HashSet<String>();
	private AbstractExpectMessage myExpect;

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
	
	
	public void validate(String theRawMessage, Message theParsedMessage) throws IncorrectMessageReceivedException {
		String value;
		try {
			value = new Terser(theParsedMessage).get(myPath);
		} catch (HL7Exception e) {
			throw new IncorrectMessageReceivedException(myExpect, theRawMessage, e.getMessage());
		}

		if (!myValues.isEmpty()) {
			if (!myValues.contains(value)) {
				throw new IncorrectMessageReceivedException(myExpect, theRawMessage, "Incorrect value for SPEC[" + myPath + "]. Expected[" + myValues + "] but found[" + value + "]");
			}
		}

		if (!myNotValues.isEmpty()) {
			if (myNotValues.contains(value)) {
				throw new IncorrectMessageReceivedException(myExpect, theRawMessage, "Incorrect value for SPEC[" + myPath + "]. Should not contain[" + myNotValues + "] but found[" + value + "]");
			}
		}

		for (Pattern next : myPatterns) {
			if (!next.matcher(value).matches()) {
				throw new IncorrectMessageReceivedException(myExpect, theRawMessage, "Incorrect value for SPEC[" + myPath + "]. Expected Pattern[" + next.pattern() + "] but found[" + value + "]");
			}
		}

		for (Pattern next : myNotPatterns) {
			if (next.matcher(value).matches()) {
				throw new IncorrectMessageReceivedException(myExpect, theRawMessage, "Incorrect value for SPEC[" + myPath + "]. Expected Not Pattern[" + myValues + "] but found[" + value + "]");
			}
		}

	}

	public static TerserRuleImpl getValuesInstance(AbstractExpectMessage theExpect, String thePath, String... theValues) {
		TerserRuleImpl retVal = new TerserRuleImpl(theExpect);
		retVal.myPath = thePath;
		
		for (String string : theValues) {
			retVal.myValues.add(string);
		}
		
		return retVal;
	}
	
	public static TerserRuleImpl getNotValuesInstance(AbstractExpectMessage theExpect, String thePath, String... theValues) {
		TerserRuleImpl retVal = new TerserRuleImpl(theExpect);
		retVal.myPath = thePath;
		
		for (String string : theValues) {
			retVal.myNotValues.add(string);
		}
		
		return retVal;
	}

}
