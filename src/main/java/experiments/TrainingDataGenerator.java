package experiments;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;

public class TrainingDataGenerator {
	
	private MedicalSequenceIterator<StateImpl> input;
	
	public TrainingDataGenerator(MedicalSequenceIterator<StateImpl> input) {
		this.input = input;
	}
	
	public MedicalSequenceIterator<StateImpl> getTrainingData(double percentage) {
		
		//Get amount of Sequences
		//Get amount of samples needed based on percentage
		//Shuffle a list from 0..size
		//Pick first amount of samples from shuffled
		//iterate and write samples to file
		//Make new iterator with new file
		//Return
		
		return null;
		
	}

}
