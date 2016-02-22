package state2vec;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.nd4j.linalg.api.ndarray.INDArray;

public class KNNLookupTable<T extends SequenceElement> {
	
	//TODO: Checken hoe precies die n closest worden bepaald -> Ik denk VPTree
	
	/**
	 * Use this class to feed in the data to the RNN!
	 */
	
	private SequenceVectors<T> vectors;
	private int nearestNeighbours;
	private List<Double> weights;
	
	public KNNLookupTable(SequenceVectors<T> vectors, int nearestNeighbours) {
		this.vectors = vectors;
		this.nearestNeighbours = nearestNeighbours;
		this.weights = calculateWeights();
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

	public INDArray addSequenceElementVector(SequenceElement sequenceElement) {
		
		String label = sequenceElement.getLabel();
		INDArray result = null;
		
		if(!vectors.hasWord(label)) {
			List<String> kNearestNeighbours = new ArrayList<String>(vectors.wordsNearestSum(label, nearestNeighbours)); // KNN lookup 
			
			List<INDArray> wordVectors = new ArrayList<INDArray>();
			for(String neighbour: kNearestNeighbours) {
				wordVectors.add(vectors.getWordVectorMatrix(neighbour));
			}
			
			// gewogen gemiddelde van de arrays = 0.8 * array1 + 0.2 + array2
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
			vectors.lookupTable().putVector(label, result);
			
		}
		else {
			result = vectors.getLookupTable().vector(label);
		}
		
		return result;
		
	}

}
