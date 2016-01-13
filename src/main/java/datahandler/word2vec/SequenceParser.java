package datahandler.word2vec;

import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;


public interface SequenceParser {
	
	public Sequence<StateImpl> getSequence(List<String []> states);

}
