package datahandler;

import java.util.Iterator;

import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;


public class MedicalSequenceIterator<T extends SequenceElement> implements SequenceIterator<T> {

	 private Iterable<Sequence<T>> underlyingIterable;
	 private Iterator<Sequence<T>> currentIterator;
	
	public MedicalSequenceIterator(Iterable<Sequence<T>> iterable) {
		this.underlyingIterable = iterable;
        this.currentIterator = iterable.iterator();
	}
	
	@Override
	public boolean hasMoreSequences() {
		return currentIterator.hasNext();
	}

	@Override
	public Sequence<T> nextSequence() {
		return currentIterator.next();
	}

	@Override
	public void reset() {
		this.currentIterator = underlyingIterable.iterator();
		
	}

}
