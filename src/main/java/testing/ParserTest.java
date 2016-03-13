package testing;


import java.io.File;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import util.Constants;

public class ParserTest {

	public static void main(String[] args) throws Exception {
		File file = new File(Constants.INPUT_CSV_TEST);

		MedicalSequenceIterator<StateImpl> sequenceIterator = new MedicalSequenceIterator<>(file);


		while(sequenceIterator.hasMoreSequences()) {
			Sequence<StateImpl> sequence = sequenceIterator.nextSequence();
			
		}

		sequenceIterator.reset();

		







	}


}
