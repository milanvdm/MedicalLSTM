package experiments;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import state2vec.KNNLookupTable;
import state2vec.State2Vec;

public class KnnTest {

	protected static final Logger logger = LoggerFactory.getLogger(KnnTest.class);

	public KnnTest(File file) throws Exception {		

		MedicalSequenceIterator<StateImpl> input = new MedicalSequenceIterator<StateImpl>(file, false);

		List<Integer> windowSizes = Arrays.asList(5, 10, 15);
		List<Double> learningRates = Arrays.asList(0.025, 0.05, 0.1);
		List<Integer> vectorLengths = Arrays.asList(50, 75, 100);
		int batchsize = 500;
		List<Integer> epochs = Arrays.asList(1, 3, 5);
		List<Double> percentages = Arrays.asList(0.80, 0.90, 0.95);
		List<Integer> ksLookup = Arrays.asList(10, 50, 100);


		MedicalSequenceIterator<StateImpl> sequenceIterator = new MedicalSequenceIterator<StateImpl>(file, false);

		for(int windowSize: windowSizes) {
			for(double learningRate: learningRates) {
				for(int vectorLength: vectorLengths) {
					for(int epoch: epochs) {
						for(double percentage: percentages) {
							TrainingDataGenerator trainingData = new TrainingDataGenerator(input, percentage);
							TestingDataGenerator testData = new TestingDataGenerator(input);

							sequenceIterator.reset();

							logger.info("KNN - EXPERIMENT");
							logger.info("");
							logger.info("==PARAMETERS==");
							logger.info("windowSize: " + windowSize);
							logger.info("learningRate: " + learningRate);
							logger.info("vectorLength: " + vectorLength);
							logger.info("batchSize: " + batchsize);
							logger.info("epoch: " + epoch);
							logger.info("");

							State2Vec state2vec = new State2Vec();
							state2vec.trainSequenceVectors(trainingData, windowSize, learningRate, vectorLength, batchsize, epoch);

							for(int kLookup: ksLookup) {
								KNNLookupTable<StateImpl> knnLookup = new KNNLookupTable<>(state2vec.getTrainedModel(), kLookup);

								Map<String, INDArray> newLabels = new HashMap<String, INDArray>();

								long startTime = System.currentTimeMillis();

								
								while(testData.hasMoreSequences()) {
									Sequence<StateImpl> sequence = testData.nextSequence();

									for(StateImpl state: sequence.getElements()) {
										INDArray result = knnLookup.addSequenceElementVector(state);
										if(result != null) {
											newLabels.put(state.getLabel(), result);
										}
									}

								}
								
								long endTime = System.currentTimeMillis();

								System.out.println("Made lookup in " + (endTime - startTime) + " milliseconds");
								
								if(newLabels.size() == 0) {
									logger.debug("NO NEW LABELS");
									continue;
								}

								List<Integer> ks = Arrays.asList(100, 1000, 5000);

								ClusterKnnTest clusterTest = new ClusterKnnTest();

								for(int k: ks) {
									ResultWriter writer1 = new ResultWriter("Knn - ", "Cluster1Test");
									writer1.writeLine("KNN - EXPERIMENT");
									writer1.writeLine("");
									writer1.writeLine("==PARAMETERS==");
									writer1.writeLine("windowSize: " + windowSize);
									writer1.writeLine("learningRate: " + learningRate);
									writer1.writeLine("vectorLength: " + vectorLength);
									writer1.writeLine("batchSize: " + batchsize);
									writer1.writeLine("epoch: " + epoch);
									writer1.writeLine("percentage: " + percentage);
									writer1.writeLine("clusterK: " + kLookup);
									writer1.writeLine("newLabels: " + newLabels.size());
									writer1.writeLine("");

									startTime = System.currentTimeMillis();
									
									clusterTest.checkClusters1(knnLookup, newLabels, k, writer1);
									
									endTime = System.currentTimeMillis();

									System.out.println("Cluster 1: " + (endTime - startTime) + " milliseconds");

									ResultWriter writer2 = new ResultWriter("Knn - ", "Cluster2Test");
									writer2.writeLine("KNN - EXPERIMENT");
									writer2.writeLine("");
									writer2.writeLine("==PARAMETERS==");
									writer2.writeLine("windowSize: " + windowSize);
									writer2.writeLine("learningRate: " + learningRate);
									writer2.writeLine("vectorLength: " + vectorLength);
									writer2.writeLine("batchSize: " + batchsize);
									writer2.writeLine("epoch: " + epoch);
									writer2.writeLine("percentage: " + percentage);
									writer2.writeLine("clusterK: " + kLookup);
									writer2.writeLine("newLabels: " + newLabels.size());
									writer2.writeLine("");

									startTime = System.currentTimeMillis();
									
									clusterTest.checkClusters2(knnLookup, newLabels, k, writer2);
									
									endTime = System.currentTimeMillis();

									System.out.println("Cluster 2: " + (endTime - startTime) + " milliseconds");
								}


							}

						}
					}
				}
			}
		}





	}

}
