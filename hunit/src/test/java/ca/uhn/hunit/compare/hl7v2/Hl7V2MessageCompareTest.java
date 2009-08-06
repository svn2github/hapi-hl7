package ca.uhn.hunit.compare.hl7v2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;


public class Hl7V2MessageCompareTest {

	@Test
	public void testFieldDifference() throws EncodingNotSupportedException, HL7Exception {
		
		String message1string = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" + 
				"PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" + 
				"PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" + 
				"ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" + 
				"OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" + 
				"OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";

		String message2string = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r\n" + 
		"PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r\n" + 
		"PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r\n" + 
		"ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r\n" + 
		"OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r\n" + 
		"OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH\r\n";
		
		PipeParser parser = new PipeParser();
		Message message1 = parser.parse(message1string);
		Message message2 = parser.parse(message2string);
		
		GroupComparison comparison = new Hl7V2MessageCompare().compare(message1, message2); 
		
		List<SegmentComparison> cmp = comparison.flattenMessage();
		SegmentComparison pidCmp = cmp.get(1);
		
		// PID-2 Same
		
		FieldComparison fieldCmp = pidCmp.getFieldComparisons().get(2);
		List<Type> firstField1 = fieldCmp.getDiffFields1();
		List<Type> firstField2 = fieldCmp.getDiffFields2();
		
		Assert.assertEquals(1, firstField1.size());
		Assert.assertEquals(1, firstField2.size());
		Assert.assertNull(firstField1.get(0));
		Assert.assertNull(firstField2.get(0));
		
		// PID-4 Different
		
		fieldCmp = pidCmp.getFieldComparisons().get(4);
		firstField1 = fieldCmp.getDiffFields1();
		firstField2 = fieldCmp.getDiffFields2();
		
		Assert.assertEquals(1, firstField1.size());
		Assert.assertEquals(1, firstField2.size());
		Assert.assertNotNull(firstField1.get(0));
		Assert.assertNotNull(firstField2.get(0));

	}
	
}
