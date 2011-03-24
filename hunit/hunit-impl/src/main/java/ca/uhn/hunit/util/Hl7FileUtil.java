package ca.uhn.hunit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Contains utility methods for dealing with HL7 files
 */
public class Hl7FileUtil {

	public static List<Message> loadFileAndParseIntoMessages(String theFileName) throws IOException, EncodingNotSupportedException, HL7Exception {
		ArrayList<Message> retVal = new ArrayList<Message>();
		PipeParser parser = PipeParser.getInstanceWithNoValidation();
		
		File file = new File(theFileName);
		if (!file.exists() || !file.isFile()) {
			throw new FileNotFoundException("Unknown file or not a file: " + theFileName);
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String string = IOUtils.toString(reader);
		reader.close();
		
		String[] messageStrings = string.split("(\\r|\\n)MSH");
		for (String nextString : messageStrings) {
			
			nextString = nextString.trim();
			if (!nextString.contains("|")) {
				continue;
			}
			if (!nextString.startsWith("MSH")) {
				nextString = "MSH" + nextString;
			}
			
			Message nextMessage = parser.parse(nextString);
			retVal.add(nextMessage);
			
		}
		
		return retVal;
	}
	
	
}
