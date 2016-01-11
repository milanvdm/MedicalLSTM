package datahandler;

import data.StateImpl;

public interface DataStreamer {

	public MedicalSequenceIterator<StateImpl> getMedicalIterator(String file);
	
}
