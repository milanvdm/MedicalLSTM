package experiments;

import java.io.File;

import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import state2vec.KNNLookupTable;
import state2vec.State2Vec;

public class KnnTest {

	//TODO: Do all experiments based on xx% of the data and added the rest with knn method. Check differences.

	public KnnTest(File file) throws Exception {		
		
		MedicalSequenceIterator<StateImpl> input = new MedicalSequenceIterator<StateImpl>(file, false);
		
		TrainingDataGenerator trainingData = new TrainingDataGenerator(input, 0.80); //TODO: multiple tests with percentage and just other random shuffles

		State2Vec state2vec = new State2Vec();
		state2vec.trainSequenceVectors(trainingData, 10, 0.025, 50, 250, 1); //TODO: Test parameters

		KNNLookupTable<StateImpl> knnLookup = new KNNLookupTable<>(state2vec.getTrainedModel(), 3); //TODO: test k

		TestingDataGenerator testData = new TestingDataGenerator(input);
		
		while(testData.hasMoreSequences()) {
			Sequence<StateImpl> sequence = testData.nextSequence();
			
			for(StateImpl state: sequence.getElements()) {
				knnLookup.addSequenceElementVector(state);
			}
			
		}
		
		SequenceVectors<StateImpl> sequenceVectors = knnLookup.getSequenceVectors();
		
		ClusterSeqTest clusterTest = new ClusterSeqTest();
		//TODO: Define tests for cluster testing;
		
		//TODO: ClusterTesting with KNNLoolupTable


	}

}
