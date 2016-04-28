package deepwalk;

import java.util.Iterator;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;
import deepwalk.transformers.impl.GraphTransformer;

public class GraphSequenceIterator implements SequenceIterator<StateImpl>  {
	
	private GraphTransformer<StateImpl> transformer;
	private Iterator<Sequence<StateImpl>> iterator;
	

	public GraphSequenceIterator(GraphTransformer<StateImpl> transformer) {
		this.transformer = transformer;
		this.iterator = transformer.iterator();
	}

	@Override
	public boolean hasMoreSequences() {
		return iterator.hasNext();
	}

	@Override
	public Sequence<StateImpl> nextSequence() {

		return iterator.next();
		
	}

	@Override
	public void reset() {
		iterator = transformer.iterator();
		
	}

}
