package experiments;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.deeplearning4j.graph.models.deepwalk.DeepWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import deepwalk.GraphGenerator;
import deepwalk.StateDeepWalk;
import deepwalk.StateGraph;

public class DeepWalkTest {

	protected static final Logger logger = LoggerFactory.getLogger(DeepWalkTest.class);


	public DeepWalkTest(File file) throws Exception {

		MedicalSequenceIterator<StateImpl> input = new MedicalSequenceIterator<StateImpl>(file, false);

		List<Integer> windowSizes = Arrays.asList(5, 10, 15);
		List<Double> learningRates = Arrays.asList(0.025, 0.05, 0.1);
		List<Integer> vectorLengths = Arrays.asList(50, 75, 100);
		List<Double> percentages = Arrays.asList(0.80, 0.90, 0.95);
		List<Integer> walkLengths = Arrays.asList(5, 10, 15);

		for(int windowSize: windowSizes) {
			for(double learningRate: learningRates) {
				for(int vectorLength: vectorLengths) {
					for(double percentage: percentages) {
						for(int walkLength: walkLengths) {
							TrainingDataGenerator trainingData = new TrainingDataGenerator(input, percentage); //TODO: extra test because of random shuffle
							//TestingDataGenerator testData = new TestingDataGenerator(input);

							input.reset();

							logger.info("DEEPWALK - EXPERIMENT");
							logger.info("");
							logger.info("==PARAMETERS==");
							logger.info("windowSize: " + windowSize);
							logger.info("learningRate: " + learningRate);
							logger.info("vectorLength: " + vectorLength);
							logger.info("percentage: " + percentage);
							logger.info("walklength: " + walkLength);
							logger.info("");


							GraphGenerator graphGenerator = new GraphGenerator(trainingData);

							StateGraph graph = graphGenerator.createGraph();
							int highestId = graphGenerator.getHighestId();

							StateDeepWalk deepwalk = new StateDeepWalk();
							deepwalk.trainDeepWalk(graph, windowSize, learningRate, vectorLength, walkLength); 
							
							DeepWalk<List<Double>, Integer> model = deepwalk.getTrainedModel();

							List<Integer> ks = Arrays.asList(100, 1000, 5000);
							
							ClusterGraphTest clusterTest = new ClusterGraphTest();
							

							for(int k: ks) {
								ResultWriter writer1 = new ResultWriter("Deepwalk - ", "Cluster1Test");
								writer1.writeLine("DEEPWALK - EXPERIMENT");
								writer1.writeLine("");
								writer1.writeLine("==PARAMETERS==");
								writer1.writeLine("windowSize: " + windowSize);
								writer1.writeLine("learningRate: " + learningRate);
								writer1.writeLine("vectorLength: " + vectorLength);
								writer1.writeLine("percentage: " + percentage);
								writer1.writeLine("walklength: " + walkLength);
								writer1.writeLine("");
								
								clusterTest.checkClusters1(model, highestId, k, writer1);
							

								ResultWriter writer2 = new ResultWriter("Deepwalk - ", "Cluster2Test");
								writer2.writeLine("DEEPWALK - EXPERIMENT");
								writer2.writeLine("");
								writer2.writeLine("==PARAMETERS==");
								writer2.writeLine("windowSize: " + windowSize);
								writer2.writeLine("learningRate: " + learningRate);
								writer2.writeLine("vectorLength: " + vectorLength);
								writer2.writeLine("percentage: " + percentage);
								writer2.writeLine("walklength: " + walkLength);
								writer2.writeLine("");

								
								clusterTest.checkClusters2(model, highestId, k, writer2);
								
							}
						}
					}
				}
			}
		}

	}





}
