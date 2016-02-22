package datahandler.lstm;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;


public class MedicalDataSet  {

	// Dont think this is needed as I only have one dataset
	// --> Make a Dataset object to feed into the network
	// Add labels to the Dataset object to train the network
	// Based on KNNLookup!
	// Label is always last timestep in serie
	// Count amount of labels because its the amount of nodes for the last layer in our network

	// 4d tensor! Many-to-One
	
	private static final long serialVersionUID = 1935520764586513365L;
    private static Logger logger = LoggerFactory.getLogger(MedicalDataSet.class);
    
    private DataSet dataset;
    private MedicalSequenceIterator<StateImpl> sequenceIterator;
    
    public MedicalDataSet(MedicalSequenceIterator<StateImpl> sequenceIterator) {
    	this.sequenceIterator = sequenceIterator;
    }
    
    private void createDataset() {
    	
    	
    	
    }


}
