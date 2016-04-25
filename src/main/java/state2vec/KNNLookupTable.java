package state2vec;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import state2vec.KDTree.SearchResult;
import util.HelpFunctions;

public class KNNLookupTable<T extends SequenceElement> {

	private static final Logger logger = LoggerFactory.getLogger(KNNLookupTable.class);

	/**
	 * Use this class to feed in the data to the RNN!
	 */

	private SequenceVectors<StateImpl> vectors;
	private int nearestNeighbours;
	private List<Double> weights;

	private INDArray columnMeans;
	private INDArray columnStds;

	private KDTree<INDArray> labelTree = null;
	private KDTree<String> vectorTree = null;


	public KNNLookupTable(SequenceVectors<StateImpl> vectors, int nearestNeighbours) {
		this.vectors = vectors;
		this.nearestNeighbours = nearestNeighbours;
		this.weights = calculateWeights();
		calculateMeanStd();
	}

	private void calculateMeanStd() {
		INDArray wordLabels = null;

		boolean first = true;
		int rowNb = 0;
		for(String word: vectors.getVocab().words()) {

			double[] label = HelpFunctions.parse(word);

			if(first) {
				wordLabels = Nd4j.create(vectors.getVocab().numWords(), label.length);

				first = false;
			}

			wordLabels.putRow(rowNb, Nd4j.create(label));

			rowNb++;
		}

		this.columnMeans = wordLabels.mean(0);
		this.columnStds = wordLabels.std(0).addi(Nd4j.scalar(Nd4j.EPS_THRESHOLD));


	}

	private List<Double> calculateWeights() {
		List<Double> weights = new ArrayList<Double>();

		double i = nearestNeighbours;
		while(i != 0) {
			weights.add(i / nearestNeighbours);
			i--;
		}

		double sum = 0;
		for(double toSum: weights) {
			sum = sum + toSum;
		}

		List<Double> toReturn = new ArrayList<Double>();
		for(double weight: weights) {
			double newWeight = weight / sum;
			toReturn.add(newWeight);
		}

		return toReturn;
	}

	public INDArray addSequenceElementVector(StateImpl sequenceElement) {

		String label = sequenceElement.getLabel();
		INDArray result = null;

		if(!vectors.hasWord(label)) {

			//logger.debug("Didn't find word in vocab!");

			List<SearchResult<INDArray>> kNearestNeighbours = nearestNeighboursLabel(sequenceElement); // KNN lookup 

			//System.out.println("KNN NEAREST");
			//System.out.println(kNearestNeighbours.toString());

			//logger.debug(Integer.toString(kNearestNeighbours.size()));

			List<INDArray> wordVectors = new ArrayList<INDArray>();
			for(SearchResult<INDArray> neighbour: kNearestNeighbours) {

				INDArray point = neighbour.payload;

				List<Double> labelList = new ArrayList<Double>();
				int i = 0;
				while(i < point.columns()) {
					double toAdd = point.getDouble(i);
					labelList.add(toAdd);
					i++;
				}

				String neighbourLabel = labelList.toString();

				wordVectors.add(vectors.getWordVectorMatrix(neighbourLabel));
			}

			// gewogen gemiddelde van de arrays = 0.8 * array1 + 0.2 * array2
			int i = 0;
			while(i < wordVectors.size()) {
				if(result == null) {
					result = wordVectors.get(i).mul(weights.get(i));
				}
				else {
					result = result.add(wordVectors.get(i).mul(weights.get(i)));
				}

				i++;
			}

			// word met vector in lookuptable steken!

			return result;

		}
		else {

			//logger.debug("Found word in vocab!");
			//result = vectors.getLookupTable().vector(label);
		}

		return null;

	}

	public SequenceVectors<StateImpl> getSequenceVectors() {
		return this.vectors;
	}

	private List<SearchResult<INDArray>> nearestNeighboursLabel(StateImpl label) {

		if(labelTree == null) { // Tree hasn't been build yet.

			labelTree = new KDTree.Euclidean<INDArray>(label.getState2vecLabel().size());

			for(StateImpl state: vectors.getVocab().vocabWords()) {
				INDArray ndarray = state.getState2vecLabelNormalized(columnMeans, columnStds);



				labelTree.addPoint(ndarray.data().asDouble(), ndarray);

			}

		}

		List<SearchResult<INDArray>> results = labelTree.nearestNeighbours(label.getState2vecLabelNormalized(columnMeans, columnStds).data().asDouble(), nearestNeighbours);

		return results;

	}

	public List<SearchResult<String>> nearestNeighboursVector(INDArray vector, int k) {

		if(vectorTree == null) { // Tree hasn't been build yet.
			
			vectorTree = new KDTree.Euclidean<String>(vectors.lookupTable().layerSize());

			for(StateImpl state: vectors.getVocab().vocabWords()) {
				INDArray ndarray = vectors.getWordVectorMatrix(state.getLabel());



				vectorTree.addPoint(ndarray.data().asDouble(), state.getLabel());

			}

		}

		List<SearchResult<String>> results = vectorTree.nearestNeighbours(vector.data().asDouble(), k);

		return results;

	}



}
