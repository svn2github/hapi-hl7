package ca.uhn.hunit.compare.hl7v2.bulk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hunit.api.IMessageTransformer;
import ca.uhn.hunit.compare.hl7v2.Hl7V2MessageCompare;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.util.Hl7FileUtil;

public class BulkHl7V2Comparison {

	private static final Logger ourLog = Logger.getLogger(BulkHl7V2Comparison.class.getName());
	
	private Iterator<Message> myActualMessages;
	private List<IMessageTransformer<Message>> myActualMessageTransformers = new ArrayList<IMessageTransformer<Message>>();
	private Iterator<Message> myExpectedMessages;
	private List<IMessageTransformer<Message>> myExpectedMessageTransformers = new ArrayList<IMessageTransformer<Message>>();
	private List<Hl7V2MessageCompare> myFailedComparisons = new ArrayList<Hl7V2MessageCompare>();
	private Set<String> myFieldsToIgnore = new HashSet<String>();
	private PipeParser myParser;
	private boolean myStopOnFirstFailure;
	private int myTotalMessages = -1;

	public BulkHl7V2Comparison() {
		myParser = PipeParser.getInstanceWithNoValidation();
	}
	
	
	public void addActualMessageTransformer(IMessageTransformer<Message> theTransformer) {
		myActualMessageTransformers.add(theTransformer);
	}
	
	public void addExpectedMessageTransformer(IMessageTransformer<Message> theTransformer) {
		myExpectedMessageTransformers.add(theTransformer);
	}

	/**
	 * @param theFieldToIgnore
	 *            the terserPathsToIgnore to set
	 */
	public void addFieldToIgnore(String theFieldToIgnore) {
		myFieldsToIgnore.add(theFieldToIgnore);
	}
	
	
	public void compare() throws UnexpectedTestFailureException {
		
		int actualIndex = 0;
		int expectedIndex = 0;
		
		while (myActualMessages.hasNext() && myExpectedMessages.hasNext()) {
						
			Message actualMessage = myActualMessages.next();
			Message expectedMessage = myExpectedMessages.next();

			for (IMessageTransformer<Message> next : myExpectedMessageTransformers) {
				expectedMessage = next.transform(expectedMessage);
			}
			for (IMessageTransformer<Message> next : myActualMessageTransformers) {
				actualMessage = next.transform(actualMessage);
			}
			
			Terser aTerser = new Terser(actualMessage);
			Terser eTerser = new Terser(expectedMessage);
			
			StringBuilder msg = new StringBuilder();
			msg.append("Comparing message " + (actualIndex + 1));
			if (myTotalMessages != -1) {
				msg.append((actualIndex + 1) + "/" + myTotalMessages);
			}
			try {
	            msg.append(" - MSH-10 Expected[" + eTerser.get("/.MSH-10") + "] Actual[" + aTerser.get("/.MSH-10") + "]");
            } catch (HL7Exception e) {
            	// ignore, just for logging
            }
            ourLog.info(msg.toString());
            
			Hl7V2MessageCompare comparison = new Hl7V2MessageCompare(myParser);
			comparison.setFieldsToIgnore(myFieldsToIgnore);
			comparison.compare(expectedMessage, actualMessage);
			
			if (!comparison.isSame()) {
				myFailedComparisons.add(comparison);
				
				if (myStopOnFirstFailure) {
					break;
				}
				
			}
			
			actualIndex++;
			expectedIndex++;
		}
		
	}

	public String describeDifferences() throws HL7Exception {
		StringBuilder retVal = new StringBuilder();
		
		for (Hl7V2MessageCompare next : myFailedComparisons) {
		
			retVal.append("Expected Message:\n");
			retVal.append(next.getExpectedMessage().encode().replace("\r", "\n"));
			retVal.append("\n\nActual Message:\n");
			retVal.append(next.getActualMessage().encode().replace("\r", "\n"));

			retVal.append("\n\nDifferences:\n");
			retVal.append(next.describeDifference());
			retVal.append("\n\n");
		}
		
		return retVal.toString();
	}

	/**
	 * @return the failedComparisons
	 */
	public List<Hl7V2MessageCompare> getFailedComparisons() {
		return myFailedComparisons;
	}

	/**
	 * @return the stopOnFirstFailure
	 */
	public boolean isStopOnFirstFailure() {
		return myStopOnFirstFailure;
	}

	/**
	 * @param theActualMessages
	 *            the actualMessages to set
	 */
	public void setActualMessages(Iterator<Message> theActualMessages) {
		myTotalMessages = -1;
		myActualMessages = theActualMessages;
	}

	/**
	 * @param theActualMessages
	 *            the actualMessages to set
	 */
	public void setActualMessages(List<Message> theActualMessages) {
		myTotalMessages = theActualMessages.size();
		myActualMessages = theActualMessages.iterator();
	}
	
	/**
	 * @param theExpectedMessages
	 *            the expectedMessages to set
	 */
	public void setExpectedMessages(Iterator<Message> theExpectedMessages) {
		myExpectedMessages = theExpectedMessages;
	}

	/**
	 * @param theExpectedMessages
	 *            the expectedMessages to set
	 */
	public void setExpectedMessages(List<Message> theExpectedMessages) {
		myExpectedMessages = theExpectedMessages.iterator();
	}


	/**
	 * @param theStopOnFirstFailure
	 *            the stopOnFirstFailure to set
	 */
	public void setStopOnFirstFailure(boolean theStopOnFirstFailure) {
		myStopOnFirstFailure = theStopOnFirstFailure;
	}
	
	
	public static void main(String[] theArgs) throws EncodingNotSupportedException, IOException, HL7Exception, UnexpectedTestFailureException {
		String expectedFile = "../../prj_Map_ADT_SIMS_EPR_ADT_UHN_RxTFC_Pojo/test/expected.txt";
		String actualFile = "../../prj_Map_ADT_SIMS_EPR_ADT_UHN_RxTFC_Pojo/test/actual.txt";
		
		BulkHl7V2Comparison tester = new BulkHl7V2Comparison();
		tester.setExpectedMessages(Hl7FileUtil.loadFileAndParseIntoMessages(expectedFile));
		tester.setActualMessages(Hl7FileUtil.loadFileAndParseIntoMessages(actualFile));
		tester.setStopOnFirstFailure(true);
		tester.addFieldToIgnore("MSH-2");
		tester.addFieldToIgnore("MSH-10");
		
		tester.compare();
		
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(tester.describeDifferences());
		
	}

}
