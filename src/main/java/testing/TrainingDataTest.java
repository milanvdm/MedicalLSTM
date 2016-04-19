package testing;

import java.io.File;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import experiments.TrainingDataGenerator;
import util.Constants;

public class TrainingDataTest {
	
	public static void main(String[] args) throws Exception {
		
		File file = new File(Constants.INPUT_CSV_TEST);
		
		MedicalSequenceIterator<StateImpl> input = new MedicalSequenceIterator<StateImpl>(file, false);
		
		TrainingDataGenerator generator = new TrainingDataGenerator(input, 0.30);
		
		System.out.println("Looping");
		while(generator.hasMoreSequences()) {
			generator.nextSequence();
		}
		
		System.out.println(input.getCount());
		System.out.println(generator.getCount());
		
	}

}
