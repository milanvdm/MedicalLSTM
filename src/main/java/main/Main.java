package main;

import java.io.File;

import org.deeplearning4j.plot.BarnesHutTsne;

import state2vec.State2Vec;
import util.Constants;

public class Main {

	public static void main(String [ ] args) throws Exception {

		mappingTest();



	}

	
	
	private static void mappingTest() {
		// TODO Auto-generated method stub
		
	}



	private static void tsneTest() throws Exception {
		File file = new File(Constants.INPUT_CSV_TEST);

		State2Vec state2vec = new State2Vec();

		state2vec.trainSequenceVectors(file);

		BarnesHutTsne tsne = new BarnesHutTsne.Builder()
				.setMaxIter(10)
				.stopLyingIteration(250)
				.learningRate(1000)
				.useAdaGrad(true)
				.theta(0.5)
				.setMomentum(0.5)
				.normalize(true)
				.usePca(true)
				.build();

		state2vec.getTrainedModel().lookupTable().plotVocab(tsne);

	}



}
