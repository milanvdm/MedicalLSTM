package testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.SequenceParser;
import datahandler.word2vec.SequenceParserImpl;
import state2vec.KNNLookupTable;
import state2vec.State2Vec;

public class KnnTest {
	
	protected static final Logger logger = LoggerFactory.getLogger(KnnTest.class);
	
	public static void main(String[] args) throws Exception {

		//File file = new File("/media/milan/Data/Thesis/Datasets/OSIM/Sorted/small_conditions_sorted.csv");
		File file = new File("/media/milan/Data/Thesis/Datasets/OSIM/Sorted/test_conditions_sorted.csv");
		
		State2Vec state2vec = new State2Vec();
		state2vec.trainSequenceVectors(file);
		
		KNNLookupTable<StateImpl> knnLookup = new KNNLookupTable<>(state2vec.getTrainedModel(), 3);
		
		String newElement = "49,01-apr-2006,2,1951,8500,65,Condition Era,36416501,[[7;Drug Era - 30 day window;30;1503297;3]]";
		String[] splitted = newElement.split(",");
		
		List<String[]> readSequence = new ArrayList<String[]>();
		readSequence.add(splitted);
		
		SequenceParser parser = new SequenceParserImpl();
		Sequence<StateImpl> sequence = parser.getSequence(readSequence);
		knnLookup.addSequenceElementVector(sequence.getElements().get(0));
		
		

	}

}
