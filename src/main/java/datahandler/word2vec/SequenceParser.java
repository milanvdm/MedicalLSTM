package datahandler.word2vec;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;


public interface SequenceParser {
	
	public void setGeneralizer(Generalizer generalizer);
	
	public Sequence<StateImpl> getSequence(List<String []> states) throws ParseException, IOException, InterruptedException;

	public void noGeneralizer();

}
