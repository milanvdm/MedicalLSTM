package experiments;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public KnnTest(File trainingFile, File testFile, String run) throws Exception {		

		List<Integer> windowSizes;

		if(run.equals("0")){
			logger.info("Run 0");
			windowSizes = Arrays.asList(5);
		}
		else if(run.equals("1")) {
			logger.info("Run 1");
			windowSizes = Arrays.asList(10);
		}
		else {
			logger.info("Run " + run);
			windowSizes = Arrays.asList(15);
		}

		//List<Integer> windowSizes = Arrays.asList(5, 10, 15);
		List<Double> learningRates = Arrays.asList(0.025, 0.1);
		List<Integer> vectorLengths = Arrays.asList(50, 100);
		List<Integer> minWordFreqs = Arrays.asList(5, 10);
		int batchsize = 500;
		int epoch = 1;

		for(int windowSize: windowSizes) {
			for(double learningRate: learningRates) {
				for(int vectorLength: vectorLengths) {
					for(int minWordFreq: minWordFreqs) {
							MedicalSequenceIterator<StateImpl> trainingData = new MedicalSequenceIterator<StateImpl>(trainingFile, false);
							MedicalSequenceIterator<StateImpl> testData = new MedicalSequenceIterator<StateImpl>(testFile, false);

							logger.info("KNN - EXPERIMENT");
							logger.info("");
							logger.info("==PARAMETERS==");
							logger.info("windowSize: " + windowSize);
							logger.info("learningRate: " + learningRate);
							logger.info("vectorLength: " + vectorLength);
							logger.info("batchSize: " + batchsize);
							logger.info("epoch: " + epoch);
							logger.info("minWordFreq: " + minWordFreq);
							logger.info("");

							State2Vec state2vec = new State2Vec();
							state2vec.trainSequenceVectors(trainingData, windowSize, learningRate, vectorLength, batchsize, epoch, minWordFreq);


							List<Integer> ksLookup = Arrays.asList(10, 50, 100);
							
							for(int kLookup: ksLookup) {
								KNNLookupTable<StateImpl> knnLookup = new KNNLookupTable<>(state2vec.getTrainedModel(), kLookup);

								Map<String, INDArray> newLabels = new HashMap<String, INDArray>();


								while(testData.hasMoreSequences()) {
									Sequence<StateImpl> sequence = testData.nextSequence();

									for(StateImpl state: sequence.getElements()) {
										INDArray result = knnLookup.addSequenceElementVector(state);
										if(result != null) {
											newLabels.put(state.getLabel(), result);
										}
									}

								}


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
									writer1.writeLine("minWordFreq: " + minWordFreq);
									writer1.writeLine("clusterK: " + kLookup);
									writer1.writeLine("newLabels: " + newLabels.size());
									writer1.writeLine("");

									clusterTest.checkClusters1(knnLookup, newLabels, k, writer1);


									ResultWriter writer2 = new ResultWriter("Knn - ", "Cluster2Test");
									writer2.writeLine("KNN - EXPERIMENT");
									writer2.writeLine("");
									writer2.writeLine("==PARAMETERS==");
									writer2.writeLine("windowSize: " + windowSize);
									writer2.writeLine("learningRate: " + learningRate);
									writer2.writeLine("vectorLength: " + vectorLength);
									writer2.writeLine("batchSize: " + batchsize);
									writer2.writeLine("epoch: " + epoch);
									writer2.writeLine("minWordFreq: " + minWordFreq);
									writer2.writeLine("clusterK: " + kLookup);
									writer2.writeLine("newLabels: " + newLabels.size());
									writer2.writeLine("");


									clusterTest.checkClusters2(knnLookup, newLabels, k, writer2);

								}


							}

						}
					}
				}
			}
		}





	

}
