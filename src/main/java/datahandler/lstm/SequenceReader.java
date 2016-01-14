package datahandler.lstm;


import org.canova.api.conf.Configuration;
import org.canova.api.io.data.DoubleWritable;
import org.canova.api.records.reader.SequenceRecordReader;
import org.canova.api.split.InputSplit;
import org.canova.api.writable.Writable;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.State;
import data.StateImpl;
import scala.NotImplementedError;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SequenceReader implements SequenceRecordReader {
    
	private static final long serialVersionUID = 2566782461516037172L;
	
	private SequenceIterator<StateImpl> sequenceIterator;

    public SequenceReader(SequenceIterator<StateImpl> sequenceIterator) {
        this.sequenceIterator = sequenceIterator;
        reset();
    }

    @Override
    public Collection<Collection<Writable>> sequenceRecord() {
    	Sequence<StateImpl> next = sequenceIterator.nextSequence();
    	
    	
    	Collection<Collection<Writable>> out = new ArrayList<>();
    	
    	for(State state: next.getElements()) {
    		ArrayList<Writable> writableState = new ArrayList<>();
    		
    		for(Double value: state.getMedicalVector()) {
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


}