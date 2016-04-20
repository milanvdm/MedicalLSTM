package experiments;


import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;

public class TestingDataGenerator implements SequenceIterator<StateImpl> {
	
	private MedicalSequenceIterator<StateImpl> input;
	
	private int count = 0;
	
	public TestingDataGenerator(MedicalSequenceIterator<StateImpl> input) {
		this.input = input;
		if(input.getTrainingData() == null) {
			throw new IllegalArgumentException();
		}
		input.reset();
	}


	@Override
	public boolean hasMoreSequences() {
		if(input.hasMoreSequences() && input.getCount() - input.getTrainingData().size() > count) {
				
			return true;
		}
		
		return false;
	}

	@Override
	public Sequence<StateImpl> nextSequence() {
		while(input.getTrainingData().contains(input.getCurrentCount())) {
			if(input.hasMoreSequences()) {
				input.nextSequence();
			}
		}
		
		count++;
		return input.nextSequence();
	}

	@Override
	public void reset() {
		input.reset();
		
	}
	
	public int getCount() {
		return count;
	}

}
