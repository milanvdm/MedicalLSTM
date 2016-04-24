package experiments;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import state2vec.State2Vec;

public class State2VecTest {
	
	protected static final Logger logger = LoggerFactory.getLogger(State2VecTest.class);
	
	public State2VecTest(File file) throws Exception {
		
		
		
		List<Integer> windowSizes = Arrays.asList(5, 10, 15);
		List<Double> learningRates = Arrays.asList(0.025, 0.05, 0.1);
		List<Integer> vectorLengths = Arrays.asList(50, 75, 100);
		int batchsize = 500;
		List<Integer> epochs = Arrays.asList(1, 3, 5);
		
		
		MedicalSequenceIterator<StateImpl> sequenceIterator = new MedicalSequenceIterator<StateImpl>(file, false);
		
		for(int windowSize: windowSizes) {
			for(double learningRate: learningRates) {
				for(int vectorLength: vectorLengths) {
					for(int epoch: epochs) {
						
						logger.info("STATE2VEC - EXPERIMENT");
						logger.info("");
						logger.info("==PARAMETERS==");
						logger.info("windowSize: " + windowSize);
						logger.info("learningRate: " + learningRate);
						logger.info("vectorLength: " + vectorLength);
						logger.info("batchSize: " + batchsize);
						logger.info("epoch: " + epoch);
						logger.info("");
						
						sequenceIterator.reset();

						State2Vec state2vec = new State2Vec();
						
						state2vec.trainSequenceVectors(sequenceIterator, windowSize, learningRate, vectorLength, batchsize, epoch);
						
						List<Integer> ks = Arrays.asList(100, 1000, 5000);
						
						ClusterSeqTest clusterTest = new ClusterSeqTest();
						
						for(int k: ks) {
							
							
							
							ResultWriter writer1 = new ResultWriter("State2Vec - ", "Cluster1Test");
							writer1.writeLine("STATE2VEC - EXPERIMENT");
							writer1.writeLine("");
							writer1.writeLine("==PARAMETERS==");
							writer1.writeLine("windowSize: " + windowSize);
							writer1.writeLine("learningRate: " + learningRate);
							writer1.writeLine("vectorLength: " + vectorLength);
							writer1.writeLine("batchSize: " + batchsize);
							writer1.writeLine("epoch: " + epoch);
							writer1.writeLine("");
							
							
							
							clusterTest.checkClusters1(state2vec.getTrainedModel(), k, writer1);
							
							
							
							ResultWriter writer2 = new ResultWriter("State2Vec - ", "Cluster2Test");
							writer2.writeLine("STATE2VEC - EXPERIMENT");
							writer2.writeLine("");
							writer2.writeLine("==PARAMETERS==");
							writer2.writeLine("windowSize: " + windowSize);
							writer2.writeLine("learningRate: " + learningRate);
							writer2.writeLine("vectorLength: " + vectorLength);
							writer2.writeLine("batchSize: " + batchsize);
							writer2.writeLine("epoch: " + epoch);
							writer2.writeLine("");
							
							clusterTest.checkClusters2(state2vec.getTrainedModel(), k, writer2);
							
					
						}
						
					}
				}
			}
		}
		
		
		
			
		
	}

}
