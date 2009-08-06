package ca.uhn.hunit.compare.hl7v2;

import java.util.Collections;
import java.util.List;

import ca.uhn.hl7v2.model.Segment;

public class SegmentComparison extends StructureComparison {

	private List<FieldComparison> myFieldComparisons;
	private Segment mySegment1;
	private Segment mySegment2;
	
	public SegmentComparison(List<FieldComparison> theFieldComparisons, Segment theSegment1, Segment theSegment2) {
		myFieldComparisons = theFieldComparisons;
		mySegment1 = theSegment1;
		mySegment2 = theSegment2;
	}

	public SegmentComparison(Segment theSegment1, Segment theSegment2) {
		mySegment1 = theSegment1;
		mySegment2 = theSegment2;
	}

	public Segment getSegment1() {
		return mySegment1;
	}

	public Segment getSegment2() {
		return mySegment2;
	}

	public List<FieldComparison> getFieldComparisons() {
		return myFieldComparisons;
	}

	@Override
	public List<SegmentComparison> flattenMessage() {
		return Collections.singletonList(this);
	}

}
