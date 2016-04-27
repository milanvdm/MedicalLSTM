package testing;

import java.io.File;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import experiments.TestingDataGenerator;
import experiments.TrainingDataGenerator;
import util.Constants;

public class TrainingDataTest {
	
	public static void main(String[] args) throws Exception {
		
		File file = new File(Constants.INPUT_CSV_TEST);
		
		MedicalSequenceIterator<StateImpl> input = new MedicalSequenceIterator<StateImpl>(file, false);
		
		TrainingDataGenerator training = new TrainingDataGenerator(input, 0.30);
		
		System.out.println("Looping");
		while(training.hasMoreSequences()) {
			System.out.println(training.nextSequence().getElements().size());
		}
		
		System.out.println(input.getCount());
		System.out.println(training.getCount());
		
		TestingDataGenerator testing = new TestingDataGenerator(input);
		
		System.out.println("Looping");
		while(testing.hasMoreSequences()) {
			testing.nextSequence();
		}
		
		System.out.println(input.getCount());
		System.out.println(testing.getCount());
		
	}

}
