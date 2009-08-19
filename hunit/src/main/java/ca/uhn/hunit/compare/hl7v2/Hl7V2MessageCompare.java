/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
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
package ca.uhn.hunit.compare.hl7v2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hunit.util.Pair;

public class Hl7V2MessageCompare {

	private GroupComparison myComparison;

	public Hl7V2MessageCompare(Message theExpectMessage, Message theActualMessage) throws HL7Exception {
		myComparison = compare(theExpectMessage, theActualMessage);
	}

	public GroupComparison getMessageComparison() {
		return myComparison;
	}
	
	private GroupComparison compare(Message theExpectMessage, Message theActualMessage) throws HL7Exception {
		GroupComparison retVal = compareGroups(theExpectMessage, theActualMessage);
		return retVal;
	}

	private GroupComparison compareGroups(Group theStructure1, Group theStructure2) throws HL7Exception {
		String[] names1 = theStructure1.getNames();
		String[] names2 = theStructure2.getNames();

		List<StructureComparison> structureComparisons = new ArrayList<StructureComparison>();

		int nameIdx1 = 0;
		int nameIdx2 = 0;

		while (nameIdx1 < names1.length || nameIdx2 < names2.length) {
			Pair<Integer> nextSameIdx = findNextSameIndex(names1, names2, nameIdx1, nameIdx2);

			// If we're at the end of the matching segments
			if (nextSameIdx == null) {
				addRemainingStructures(theStructure1, nameIdx1, names1.length, structureComparisons, true);
				addRemainingStructures(theStructure2, nameIdx2, names2.length, structureComparisons, false);
				break;
			}

			addRemainingStructures(theStructure1, nameIdx1, nextSameIdx.getValue1(), structureComparisons, true);
			addRemainingStructures(theStructure2, nameIdx2, nextSameIdx.getValue2(), structureComparisons, false);

			
			Structure[] children1 = theStructure1.getAll(theStructure1.getNames()[nextSameIdx.getValue1()]);
			Structure[] children2 = theStructure2.getAll(theStructure2.getNames()[nextSameIdx.getValue2()]);
			int lowerCommonIndex = children1.length < children2.length ? children1.length : children2.length;
			for (int i = 0; i < lowerCommonIndex; i++) {
				Structure child1 = children1[i];
				Structure child2 = children2[i];
				if (child1 instanceof Segment) {
					structureComparisons.add(compareSegments((Segment) child1, (Segment) child2));
				} else {
					structureComparisons.add(compareGroups((Group) child1, (Group) child2));
				}
			}
			for (int i = lowerCommonIndex; i < children1.length; i++) {
				if (children1[i] instanceof Segment) {
					structureComparisons.add(new SegmentComparison(children1[i].getName(), (Segment) children1[i], null));
				} else {
					structureComparisons.add(new GroupComparison((Group) children1[i], null));
				}
			}
			for (int i = lowerCommonIndex; i < children2.length; i++) {
				if (children2[i] instanceof Segment) {
					structureComparisons.add(new SegmentComparison(children2[i].getName(), (Segment) children2[i], null));
				} else {
					structureComparisons.add(new SegmentComparison(children2[i].getName(), null, (Segment) children2[i]));
				}
			}

			nameIdx1 = nextSameIdx.getValue1() + 1;
			nameIdx2 = nextSameIdx.getValue2() + 1;
		}

		return new GroupComparison(structureComparisons);
	}

	private void addRemainingStructures(Group theStructure, int theStartingNameIndex, int theAfterEndingIndex, List<StructureComparison> theStructureComparisons, boolean theIsMessage1) throws HL7Exception {
		String[] names = theStructure.getNames();
		for (int i = theStartingNameIndex; i < theAfterEndingIndex; i++) {
			Structure[] reps = theStructure.getAll(names[i]);
			for (Structure structure : reps) {
				if (structure instanceof Group) {
					theStructureComparisons.add(new ExtraGroup((Group) structure, theIsMessage1));
				} else {
					if (theIsMessage1) {
						theStructureComparisons.add(new SegmentComparison(structure.getName(), (Segment) structure, null));
					} else {
						theStructureComparisons.add(new SegmentComparison(structure.getName(), null, (Segment) structure));
					}
				}
			}
		}
	}

	public static Pair<Integer> findNextSameIndex(String[] theStrings1, String[] theStrings2, int theStartingIndex1, int theStartingIndex2) {

		Pair<Integer> found1 = null;
		BOTH: for (int i1 = theStartingIndex1; i1 < theStrings1.length; i1++) {
			for (int i2 = theStartingIndex2; i2 < theStrings2.length; i2++) {
				if (StringUtils.equals(theStrings1[i1], theStrings2[i2])) {
					found1 = new Pair<Integer>(i1, i2);
					break BOTH;
				}
			}
		}

		Pair<Integer> found2 = null;
		BOTH: for (int i2 = theStartingIndex2; i2 < theStrings2.length; i2++) {
			for (int i1 = theStartingIndex1; i1 < theStrings1.length; i1++) {
				if (StringUtils.equals(theStrings1[i1], theStrings2[i2])) {
					found2 = new Pair<Integer>(i1, i2);
					break BOTH;
				}
			}
		}

		if (found1 == null) {
			return found2;
		} else if (found2 == null) {
			return found1;
		} else if (found1.getValue1() < found2.getValue1()) {
			return found1;
		} else {
			return found2;
		}
	}

	private SegmentComparison compareSegments(Segment theSegment1, Segment theSegment2) throws HL7Exception {
		assert theSegment1.getName().equals(theSegment2.getName());
		
		List<FieldComparison> fieldComparisons = new ArrayList<FieldComparison>();
		for (int i = 0; i < theSegment1.numFields(); i++) {
			FieldComparison nextFieldComparison = compareFields(theSegment1, theSegment2, i);
			fieldComparisons.add(nextFieldComparison);
		}
		return new SegmentComparison(theSegment1.getName(), fieldComparisons);
	}

	private FieldComparison compareFields(Segment theSegment1, Segment theSegment2, int theI) throws HL7Exception {
		Type[] reps1 = theSegment1.getField(theI + 1);
		Type[] reps2 = theSegment2.getField(theI + 1);
		int maxReps = reps1.length > reps2.length ? reps1.length : reps2.length;

		List<Type> sameFields = new ArrayList<Type>();
		List<Type> diffFields1 = new ArrayList<Type>();
		List<Type> diffFields2 = new ArrayList<Type>();

		for (int i = 0; i < maxReps; i++) {

			if (i >= reps1.length) {
				sameFields.add(null);
				diffFields1.add(null);
				diffFields2.add(reps2[i]);
			} else if (i >= reps2.length) {
				sameFields.add(null);
				diffFields1.add(reps1[i]);
				diffFields2.add(null);
			} else {
				if (compareTypes(reps1[i], reps2[i])) {
					sameFields.add(reps1[i]);
					diffFields1.add(null);
					diffFields2.add(null);
				} else {
					sameFields.add(null);
					diffFields1.add(reps1[i]);
					diffFields2.add(reps2[i]);
				}
			}

		}

		return new FieldComparison(theSegment1.getNames()[theI], sameFields, diffFields1, diffFields2);
	}

	private boolean compareTypes(Type theType1, Type theType2) {
		if (theType1 instanceof Primitive && theType2 instanceof Primitive) {
			Primitive type1 = (Primitive) theType1;
			Primitive type2 = (Primitive) theType2;
			return StringUtils.equals(type1.getValue(), type2.getValue());
		} else if (theType1 instanceof Varies && theType2 instanceof Varies) {
			Varies type1 = (Varies) theType1;
			Varies type2 = (Varies) theType2;
			return compareTypes(type1.getData(), type2.getData());
		} else if (theType1 instanceof Composite && theType2 instanceof Composite) {
			Composite type1 = (Composite) theType1;
			Composite type2 = (Composite) theType2;
			Type[] components1 = type1.getComponents();
			Type[] components2 = type2.getComponents();
			if (components1.length != components2.length) {
				return false;
			}
			for (int i = 0; i < components1.length; i++) {
				if (!compareTypes(components1[i], components2[i])) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isSame() {
		return myComparison.isSame();
	}
}
