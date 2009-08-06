package ca.uhn.hunit.compare.hl7v2;

import java.util.ArrayList;
import java.util.List;

public class GroupComparison extends StructureComparison {

	private List<StructureComparison> myGroupComparisons;

	public GroupComparison(List<StructureComparison> theStructureComparison) {
		myGroupComparisons = theStructureComparison;
	}

	public List<StructureComparison> getGroupComparisons() {
		return myGroupComparisons;
	}

	@Override
	public List<SegmentComparison> flattenMessage() {
		List<SegmentComparison> retVal = new ArrayList<SegmentComparison>();
		for (StructureComparison structureComparison : myGroupComparisons) {
			retVal.addAll(structureComparison.flattenMessage());
		}
		return retVal;
	}

}
