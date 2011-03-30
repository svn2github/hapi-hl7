package ca.uhn.hunit.compare.hl7v2;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hunit.compare.hl7v2.bulk.BulkHl7V2Comparison;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;

public class TestBulkHl7V2Comparison {

	@Test
	public void testAllSame() throws EncodingNotSupportedException, HL7Exception, UnexpectedTestFailureException {
		
        String message1expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message1actualString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169839|T|2.3\r\n" +
            "PID|||12345^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2actualString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169839|T|2.3\r\n" +
            "PID|||12345^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";
        
        PipeParser parser = new PipeParser();

        Message message1expected = parser.parse(message1expectedString);
        Message message1actual = parser.parse(message1actualString);
        Message message2expected = parser.parse(message2expectedString);
        Message message2actual = parser.parse(message2actualString);
		
		List<Message> expected = new ArrayList<Message>();
		expected.add(message1expected);
		expected.add(message2expected);
		
		List<Message> actual = new ArrayList<Message>();
		actual.add(message1actual);
		actual.add(message2actual);
		
		BulkHl7V2Comparison cmp = new BulkHl7V2Comparison();
		cmp.setActualMessages(actual);
		cmp.setExpectedMessages(expected);

		cmp.compare();
		Assert.assertEquals(0, cmp.getFailedComparisons().size());
	}

	
	@Test
	public void testControlIdsDifferent() throws EncodingNotSupportedException, HL7Exception, UnexpectedTestFailureException {
		
        String message1expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message1actualString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|10169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169839|T|2.3\r\n" +
            "PID|||12345^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2actualString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|10169839|T|2.3\r\n" +
            "PID|||12345^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";
        
        PipeParser parser = new PipeParser();

        Message message1expected = parser.parse(message1expectedString);
        Message message1actual = parser.parse(message1actualString);
        Message message2expected = parser.parse(message2expectedString);
        Message message2actual = parser.parse(message2actualString);
		
		List<Message> expected = new ArrayList<Message>();
		expected.add(message1expected);
		expected.add(message2expected);
		
		List<Message> actual = new ArrayList<Message>();
		actual.add(message1actual);
		actual.add(message2actual);
		
		BulkHl7V2Comparison cmp = new BulkHl7V2Comparison();
		cmp.setActualMessages(actual);
		cmp.setExpectedMessages(expected);
		cmp.compare();
		Assert.assertEquals(2, cmp.getFailedComparisons().size());
		
		cmp = new BulkHl7V2Comparison();
		cmp.setActualMessages(actual);
		cmp.setExpectedMessages(expected);
		cmp.addFieldToIgnore("MSH-10");
		cmp.compare();
		System.out.println("Difference was: " + cmp.describeDifferences());
		Assert.assertEquals(0, cmp.getFailedComparisons().size());
		
	}

	
	@Test
	public void testFieldValueCompositeDifferent() throws EncodingNotSupportedException, HL7Exception, UnexpectedTestFailureException {
		
        String message1expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message1actualString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169839|T|2.3\r\n" +
            "PID|||12345^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2actualString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169839|T|2.3\r\n" +
            "PID|||12345^^^ZZZ^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";
        
        PipeParser parser = new PipeParser();

        Message message1expected = parser.parse(message1expectedString);
        Message message1actual = parser.parse(message1actualString);
        Message message2expected = parser.parse(message2expectedString);
        Message message2actual = parser.parse(message2actualString);
		
		List<Message> expected = new ArrayList<Message>();
		expected.add(message1expected);
		expected.add(message2expected);
		
		List<Message> actual = new ArrayList<Message>();
		actual.add(message1actual);
		actual.add(message2actual);
		
		BulkHl7V2Comparison cmp = new BulkHl7V2Comparison();
		cmp.setActualMessages(actual);
		cmp.setExpectedMessages(expected);
		cmp.compare();
		Assert.assertEquals(1, cmp.getFailedComparisons().size());
		
		cmp = new BulkHl7V2Comparison();
		cmp.setActualMessages(actual);
		cmp.setExpectedMessages(expected);
		cmp.addFieldToIgnore("PID-3-4");
		cmp.compare();
		System.out.println("Difference was: " + cmp.describeDifferences());
		Assert.assertEquals(0, cmp.getFailedComparisons().size());
		
	}

	
	@Test
	public void testFieldValuePrimitiveDifferent() throws EncodingNotSupportedException, HL7Exception, UnexpectedTestFailureException {
		
        String message1expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message1actualString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169839|T|2.3\r\n" +
            "PID|||12345^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2actualString =
            "MSH|^~\\&|ZZZ|TML|OLIS|OLIS|200905011130||ORU^R01|20169839|T|2.3\r\n" +
            "PID|||12345^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";
        
        PipeParser parser = new PipeParser();

        Message message1expected = parser.parse(message1expectedString);
        Message message1actual = parser.parse(message1actualString);
        Message message2expected = parser.parse(message2expectedString);
        Message message2actual = parser.parse(message2actualString);
		
		List<Message> expected = new ArrayList<Message>();
		expected.add(message1expected);
		expected.add(message2expected);
		
		List<Message> actual = new ArrayList<Message>();
		actual.add(message1actual);
		actual.add(message2actual);
		
		BulkHl7V2Comparison cmp = new BulkHl7V2Comparison();
		cmp.setActualMessages(actual);
		cmp.setExpectedMessages(expected);
		cmp.compare();
		Assert.assertEquals(1, cmp.getFailedComparisons().size());
		
		cmp = new BulkHl7V2Comparison();
		cmp.setActualMessages(actual);
		cmp.setExpectedMessages(expected);
		cmp.addFieldToIgnore("MSH-3-1");
		cmp.compare();
		System.out.println("Difference was: " + cmp.describeDifferences());
		Assert.assertEquals(0, cmp.getFailedComparisons().size());
		
	}

	
	@Test
	public void testFieldValuePrimitiveWithExtraComponentDifferent() throws EncodingNotSupportedException, HL7Exception, UnexpectedTestFailureException {
		
        String message1expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message1actualString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" +
            "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2expectedString =
            "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169839^XXX|T|2.3\r\n" +
            "PID|||12345^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

        String message2actualString =
            "MSH|^~\\&|ULTRA^|TML|OLIS|OLIS|200905011130||ORU^R01|20169839^ZZZ|T|2.3\r\n" +
            "PID|||12345^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||100 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" +
            "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" +
            "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" +
            "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" +
            "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";
        
        PipeParser parser = new PipeParser();

        Message message1expected = parser.parse(message1expectedString);
        Message message1actual = parser.parse(message1actualString);
        Message message2expected = parser.parse(message2expectedString);
        Message message2actual = parser.parse(message2actualString);
		
		List<Message> expected = new ArrayList<Message>();
		expected.add(message1expected);
		expected.add(message2expected);
		
		List<Message> actual = new ArrayList<Message>();
		actual.add(message1actual);
		actual.add(message2actual);
		
		BulkHl7V2Comparison cmp = new BulkHl7V2Comparison();
		cmp.setActualMessages(actual);
		cmp.setExpectedMessages(expected);
		cmp.compare();
		Assert.assertEquals(1, cmp.getFailedComparisons().size());
		
		cmp = new BulkHl7V2Comparison();
		cmp.setActualMessages(actual);
		cmp.setExpectedMessages(expected);
		cmp.addFieldToIgnore("MSH-10-1");
		cmp.compare();
		System.out.println("Difference was: " + cmp.describeDifferences());
		Assert.assertEquals(0, cmp.getFailedComparisons().size());
		
	}
	
}
