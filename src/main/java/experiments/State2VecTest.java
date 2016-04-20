package experiments;

import java.io.File;

import state2vec.State2Vec;

public class State2VecTest {
	
	public State2VecTest(File file) throws Exception {
		
		State2Vec state2vec = new State2Vec();
		state2vec.trainSequenceVectors(file);
		//TODO: Test parameters training vectors
		
		ClusterSeqTest clusterTest = new ClusterSeqTest();
		//TODO: Define tests for cluster testing;
		
			
		
	}

}
