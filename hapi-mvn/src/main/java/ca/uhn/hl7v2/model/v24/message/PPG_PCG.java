package ca.uhn.hl7v2.model.v24.message;

import ca.uhn.log.HapiLogFactory;
import ca.uhn.hl7v2.model.v24.group.*;

import ca.uhn.hl7v2.model.v24.segment.*;

import ca.uhn.hl7v2.HL7Exception;

import ca.uhn.hl7v2.parser.ModelClassFactory;

import ca.uhn.hl7v2.parser.DefaultModelClassFactory;

import ca.uhn.hl7v2.model.AbstractMessage;

/**
 * <p>Represents a PPG_PCG message structure (see chapter 12). This structure contains the 
 * following elements: </p>
 * 0: MSH (Message Header) <b></b><br>
 * 1: PID (Patient identification) <b></b><br>
 * 2: PPG_PCG_PATIENT_VISIT (a Group object) <b>optional </b><br>
 * 3: PPG_PCG_PATHWAY (a Group object) <b>repeating</b><br>
 */
public class PPG_PCG extends AbstractMessage  {

	/** 
	 * Creates a new PPG_PCG Group with custom ModelClassFactory.
	 */
	public PPG_PCG(ModelClassFactory factory) {
	   super(factory);
	   init(factory);
	}

	/**
	 * Creates a new PPG_PCG Group with DefaultModelClassFactory. 
	 */ 
	public PPG_PCG() { 
	   super(new DefaultModelClassFactory());
	   init(new DefaultModelClassFactory());
	}

	private void init(ModelClassFactory factory) {
	   try {
	      this.add(MSH.class, true, false);
	      this.add(PID.class, true, false);
	      this.add(PPG_PCG_PATIENT_VISIT.class, false, false);
	      this.add(PPG_PCG_PATHWAY.class, true, true);
	   } catch(HL7Exception e) {
	      HapiLogFactory.getHapiLog(this.getClass()).error("Unexpected error creating PPG_PCG - this is probably a bug in the source code generator.", e);
	   }
	}

	/**
	 * Returns MSH (Message Header) - creates it if necessary
	 */
	public MSH getMSH() { 
	   MSH ret = null;
	   try {
	      ret = (MSH)this.get("MSH");
	   } catch(HL7Exception e) {
	      HapiLogFactory.getHapiLog(this.getClass()).error("Unexpected error accessing data - this is probably a bug in the source code generator.", e);
	      throw new RuntimeException(e);
	   }
	   return ret;
	}

	/**
	 * Returns PID (Patient identification) - creates it if necessary
	 */
	public PID getPID() { 
	   PID ret = null;
	   try {
	      ret = (PID)this.get("PID");
	   } catch(HL7Exception e) {
	      HapiLogFactory.getHapiLog(this.getClass()).error("Unexpected error accessing data - this is probably a bug in the source code generator.", e);
	      throw new RuntimeException(e);
	   }
	   return ret;
	}

	/**
	 * Returns PPG_PCG_PATIENT_VISIT (a Group object) - creates it if necessary
	 */
	public PPG_PCG_PATIENT_VISIT getPATIENT_VISIT() { 
	   PPG_PCG_PATIENT_VISIT ret = null;
	   try {
	      ret = (PPG_PCG_PATIENT_VISIT)this.get("PATIENT_VISIT");
	   } catch(HL7Exception e) {
	      HapiLogFactory.getHapiLog(this.getClass()).error("Unexpected error accessing data - this is probably a bug in the source code generator.", e);
	      throw new RuntimeException(e);
	   }
	   return ret;
	}

	/**
	 * Returns  first repetition of PPG_PCG_PATHWAY (a Group object) - creates it if necessary
	 */
	public PPG_PCG_PATHWAY getPATHWAY() { 
	   PPG_PCG_PATHWAY ret = null;
	   try {
	      ret = (PPG_PCG_PATHWAY)this.get("PATHWAY");
	   } catch(HL7Exception e) {
	      HapiLogFactory.getHapiLog(this.getClass()).error("Unexpected error accessing data - this is probably a bug in the source code generator.", e);
	      throw new RuntimeException(e);
	   }
	   return ret;
	}

	/**
	 * Returns a specific repetition of PPG_PCG_PATHWAY
	 * (a Group object) - creates it if necessary
	 * throws HL7Exception if the repetition requested is more than one 
	 *     greater than the number of existing repetitions.
	 */
	public PPG_PCG_PATHWAY getPATHWAY(int rep) throws HL7Exception { 
	   return (PPG_PCG_PATHWAY)this.get("PATHWAY", rep);
	}

	/** 
	 * Returns the number of existing repetitions of PPG_PCG_PATHWAY 
	 */ 
	public int getPATHWAYReps() { 
	    int reps = -1; 
	    try { 
	        reps = this.getAll("PATHWAY").length; 
	    } catch (HL7Exception e) { 
	        String message = "Unexpected error accessing data - this is probably a bug in the source code generator."; 
	        HapiLogFactory.getHapiLog(this.getClass()).error(message, e); 
	        throw new RuntimeException(message);
	    } 
	    return reps; 
	} 

}