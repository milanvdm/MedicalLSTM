package experiments;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import deepwalk.GraphGenerator;
import deepwalk.StateDeepWalk;
import deepwalk.StateGraph;

public class DeepWalkTest {

	protected static final Logger logger = LoggerFactory.getLogger(DeepWalkTest.class);


	public DeepWalkTest(File file, String run) throws Exception {

		List<Integer> windowSizes;
		List<Double> learningRates;
		
		if(run.equals("0")){
			logger.info("Run 0");
			windowSizes = Arrays.asList(5);
			learningRates = Arrays.asList(0.025, 0.1);
		}
		else if(run.equals("1")) {
			logger.info("Run 1");
			windowSizes = Arrays.asList(5);
			learningRates = Arrays.asList(0.1);
		}
		else if(run.equals("2")) {
			logger.info("Run 2");
			windowSizes = Arrays.asList(10);
			learningRates = Arrays.asList(0.025);
		}
		else if(run.equals("3")) {
			logger.info("Run 3");
			windowSizes = Arrays.asList(10);
			learningRates = Arrays.asList(0.1);
		}
		else if(run.equals("4")) {
			logger.info("Run 4");
			windowSizes = Arrays.asList(15);
			learningRates = Arrays.asList(0.025);
		}
		else {
			logger.info("Run " + run);
			windowSizes = Arrays.asList(15);
			learningRates = Arrays.asList(0.1);
		}

		//List<Integer> windowSizes = Arrays.asList(5, 10, 15);
		//List<Double> learningRates = Arrays.asList(0.025, 0.1);
		List<Integer> vectorLengths = Arrays.asList(50, 100);
		List<Integer> minWordFreqs = Arrays.asList(5, 10);
		int batchsize = 500;
		int epoch = 1;

		List<Integer> walkLengths = Arrays.asList(5, 10, 15);
		
		MedicalSequenceIterator<StateImpl> sequenceIterator = new MedicalSequenceIterator<StateImpl>(file, false);

		for(int windowSize: windowSizes) {
			for(double learningRate: learningRates) {
				for(int vectorLength: vectorLengths) {
					for(int minWordFreq: minWordFreqs) {
						for(int walkLength: walkLengths) {

							logger.info("DEEPWALK - EXPERIMENT");
							logger.info("");
							logger.info("==PARAMETERS==");
							logger.info("windowSize: " + windowSize);
							logger.info("learningRate: " + learningRate);
							logger.info("vectorLength: " + vectorLength);
							logger.info("batchSize: " + batchsize);
							logger.info("epoch: " + epoch);
							logger.info("minWordFreq: " + minWordFreq);
							logger.info("walklength: " + walkLength);
							logger.info("");


							GraphGenerator graphGenerator = new GraphGenerator(sequenceIterator);

							StateGraph graph = graphGenerator.createGraph();

							StateDeepWalk deepwalk = new StateDeepWalk();
							deepwalk.trainDeepWalk(graph, windowSize, learningRate, vectorLength, walkLength, batchsize, epoch, minWordFreq); 

							List<Integer> ks = Arrays.asList(100, 1000, 5000);

							logger.info("Started Tests");

							ClusterSeqTest clusterTest = new ClusterSeqTest();


							for(int k: ks) {
								ResultWriter writer1 = new ResultWriter("Deepwalk - ", "Cluster1Test");
								writer1.writeLine("DEEPWALK - EXPERIMENT");
								writer1.writeLine("");
								writer1.writeLine("==PARAMETERS==");
								writer1.writeLine("windowSize: " + windowSize);
								writer1.writeLine("learningRate: " + learningRate);
								writer1.writeLine("vectorLength: " + vectorLength);
								writer1.writeLine("batchSize: " + batchsize);
								writer1.writeLine("epoch: " + epoch);
								writer1.writeLine("minWordFreq: " + minWordFreq);
								writer1.writeLine("walklength: " + walkLength);
								writer1.writeLine("");

								clusterTest.checkClusters1(deepwalk.getTrainedModel(), k, writer1);


								ResultWriter writer2 = new ResultWriter("Deepwalk - ", "Cluster2Test");
								writer2.writeLine("DEEPWALK - EXPERIMENT");
								writer2.writeLine("");
								writer2.writeLine("==PARAMETERS==");
								writer2.writeLine("windowSize: " + windowSize);
								writer2.writeLine("learningRate: " + learningRate);
								writer2.writeLine("vectorLength: " + vectorLength);
								writer2.writeLine("batchSize: " + batchsize);
								writer2.writeLine("epoch: " + epoch);
								writer2.writeLine("minWordFreq: " + minWordFreq);
								writer2.writeLine("walklength: " + walkLength);
								writer2.writeLine("");


								clusterTest.checkClusters2(deepwalk.getTrainedModel(), k, writer2);

							}
						}
					}
				}
			}
		}

	}





}
