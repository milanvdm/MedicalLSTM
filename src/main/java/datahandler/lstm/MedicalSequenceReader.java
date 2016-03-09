package datahandler.lstm;


import org.canova.api.conf.Configuration;
import org.canova.api.io.data.DoubleWritable;
import org.canova.api.records.reader.SequenceRecordReader;
import org.canova.api.split.InputSplit;
import org.canova.api.writable.Writable;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.nd4j.linalg.api.ndarray.INDArray;

import data.StateImpl;
import scala.NotImplementedError;
import state2vec.KNNLookupTable;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MedicalSequenceReader implements SequenceRecordReader {
    
	private static final long serialVersionUID = 2566782461516037172L;
	
	private SequenceIterator<StateImpl> sequenceIterator;
	private KNNLookupTable<StateImpl> lookupTable;
	private List<String> labels = new ArrayList<String>();

    public MedicalSequenceReader(SequenceIterator<StateImpl> sequenceIterator, KNNLookupTable<StateImpl> knnLookupTable) {
        this.sequenceIterator = sequenceIterator;
        this.lookupTable = knnLookupTable;
        reset();
    }

    @Override
    public Collection<Collection<Writable>> sequenceRecord() {
    	Sequence<StateImpl> next = sequenceIterator.nextSequence();
    	
    	Collection<Collection<Writable>> out = new ArrayList<>();
    	
    	for(StateImpl state: next.getElements()) {
    		ArrayList<Writable> writableState = new ArrayList<>();
    		
    		INDArray baseVector = lookupTable.addSequenceElementVector(state);
    		INDArray stateVector = state.getLstmVector(baseVector);
    		
    		
    		for(double value: stateVector.data().asDouble()) {
    			writableState.add(new DoubleWritable(value));
    		}
    		
    		out.add(writableState);
    	}

        return out;
    }

    

	@Override
	public List<String> getLabels() {
		throw new NotImplementedError();
	}

	@Override
	public boolean hasNext() {
		return sequenceIterator.hasMoreSequences();
	}

	@Override
	public void initialize(InputSplit arg0) throws IOException, InterruptedException {
		throw new NotImplementedError();
		
	}

	@Override
	public void initialize(Configuration arg0, InputSplit arg1) throws IOException, InterruptedException {
		throw new NotImplementedError();
		
	}

	@Override
	public Collection<Writable> next() {
		throw new NotImplementedError();
	}

	@Override
	public void reset() {
		sequenceIterator.reset();
		
	}

	@Override
	public void close() throws IOException {
		throw new NotImplementedError();
	}

	@Override
	public Configuration getConf() {
		throw new NotImplementedError();
	}

	@Override
	public void setConf(Configuration arg0) {
		throw new NotImplementedError();
		
	}

	@Override
	public Collection<Writable> record(URI uri, DataInputStream dataInputStream) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Collection<Writable>> sequenceRecord(URI uri, DataInputStream dataInputStream)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


}