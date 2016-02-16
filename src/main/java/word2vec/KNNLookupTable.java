package word2vec;

import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;

public class KNNLookupTable<T extends SequenceElement> {
	
	/**
	 * Use this class to feed in the data to the RNN!
	 */
	
	private SequenceVectors<T> vectors;
	
	public KNNLookupTable(SequenceVectors<T> vectors) {
		this.vectors = vectors;
	}
	
	public void TODO() {
		if(!vectors.hasWord(word)) {
			vectors.wordsNearestSum(word, n) // KNN lookup 
		}
		
	}

}
