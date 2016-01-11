package datahandler;

import java.io.File;

import data.StateImpl;

public interface DataStreamer {

	public MedicalSequenceIterator<StateImpl> getMedicalIterator(File file);
	
}
