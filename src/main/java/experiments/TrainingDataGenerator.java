package experiments;


import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;

public class TrainingDataGenerator implements SequenceIterator<StateImpl> {
	
	//TOO SLOW
	
	private MedicalSequenceIterator<StateImpl> input;
	
	private double percentage;
	
	private int count = 0;
	
	public TrainingDataGenerator(MedicalSequenceIterator<StateImpl> input, double percentage) {
		this.input = input;
		this.input.generateTrainingData(percentage);
		this.percentage = percentage;
	}


	@Override
	public boolean hasMoreSequences() {
		//if(input.hasMoreSequences() && input.getCurrentCount() < input.getTrainingData().get(input.getTrainingData().size() - 1)) {
		if(input.hasMoreSequences() && input.getTrainingData().size() > count) {
			return true;
		}
		
		return false;
	}

	@Override
	public Sequence<StateImpl> nextSequence() {
		while(!input.getTrainingData().contains(input.getCurrentCount())) {
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
