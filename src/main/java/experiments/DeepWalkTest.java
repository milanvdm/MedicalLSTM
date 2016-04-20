package experiments;

import java.io.File;
import java.io.IOException;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import deepwalk.GraphGenerator;
import deepwalk.StateDeepWalk;
import deepwalk.StateGraph;

public class DeepWalkTest {
	
	public DeepWalkTest(File file) throws IOException, InterruptedException {
		//TODO: Compare results of DeepWalk to Clusters
		
		MedicalSequenceIterator<StateImpl> input = new MedicalSequenceIterator<StateImpl>(file, false);
		
		TrainingDataGenerator trainingData = new TrainingDataGenerator(input, 0.80); //TODO: multiple tests with percentage and just other random shuffles
		
		GraphGenerator graphGenerator = new GraphGenerator(trainingData);
		
		StateGraph graph = graphGenerator.createGraph();
		
		StateDeepWalk deepwalk = new StateDeepWalk();
		deepwalk.trainDeepWalk(graph, 5, 0.025, 50, 5); //TODO: test parameters
		
		
		
	}
	
	
	
	
	
}
