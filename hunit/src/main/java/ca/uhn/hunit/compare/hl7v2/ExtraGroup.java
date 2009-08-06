package ca.uhn.hunit.compare.hl7v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;

public class ExtraGroup extends StructureComparison {

	private Group myGroup;
	private boolean myMessage1;

	public ExtraGroup(Group theGroup, boolean theMessage1) {
		myGroup = theGroup;
		myMessage1 = theMessage1;
	}

	public Group getGroup() {
		return myGroup;
	}

	public boolean isMessage1() {
		return myMessage1;
	}

	@Override
	public List<SegmentComparison> flattenMessage() {
		return flatten(myGroup);
	}

	private List<SegmentComparison> flatten(Structure theStructure) {
		if (theStructure instanceof Segment) {
			if (myMessage1) {
				return Collections.singletonList((SegmentComparison)new SegmentComparison((Segment) theStructure, null));
			} else {
				return Collections.singletonList((SegmentComparison)new SegmentComparison(null, (Segment) theStructure));
			}
		}
		
		ArrayList<SegmentComparison> retVal = new ArrayList<SegmentComparison>();
		Group group = (Group)theStructure;
		for (String nextName : group.getNames()) {
			try {
				for (Structure nextRep : group.getAll(nextName)) {
					retVal.addAll(flatten(nextRep));
				}
			} catch (HL7Exception e) {
				throw new Error(e);
			}
		}
		
		return retVal;
	}

}
