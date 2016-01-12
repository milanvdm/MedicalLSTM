package datahandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import data.StateImpl;

public interface DataStreamer {

	public MedicalSequenceIterator<StateImpl> getMedicalIterator(File file) throws FileNotFoundException, IOException;
	
}
