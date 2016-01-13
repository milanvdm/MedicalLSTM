package datahandler.word2vec;

import java.util.Iterator;

import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MedicalSequenceIterator<T extends SequenceElement> implements SequenceIterator<T> {

	protected static final Logger logger = LoggerFactory.getLogger(MedicalSequenceIterator.class);

	private Iterable<Sequence<T>> underlyingIterable;
	private Iterator<Sequence<T>> currentIterator;

	public MedicalSequenceIterator(Iterable<Sequence<T>> iterable) {
		logger.info("Made Sequence Iterator");
		
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
