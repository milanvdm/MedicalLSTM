package datahandler.word2vec;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import data.StateImpl;
import util.Constants;
import util.CsvIterator;

public class MedicalSequenceIterator<T extends SequenceElement> implements SequenceIterator<T> {

	protected static final Logger logger = LoggerFactory.getLogger(MedicalSequenceIterator.class);

	private SequenceParser sequenceParser = null;
	private int previousId = -1;

	private File underlyingIterable;
	private CsvIterator currentIterator;

	private boolean useNormaliser;

	private DataNormaliser normaliser = null;

	public MedicalSequenceIterator(File file, boolean useNormaliser) throws IOException, InterruptedException {
		logger.info("Made Sequence Iterator");

		this.useNormaliser = useNormaliser;

		this.underlyingIterable = file;
		this.currentIterator = new CsvIterator(underlyingIterable);
		currentIterator.next(); //ignore first line
	}

	public void setNormaliser(DataNormaliser normaliser) {
		this.normaliser = normaliser;
	}

	@Override
	public boolean hasMoreSequences() {
		return currentIterator.hasNext();
	}

	private String[] previousLine = null;

	@SuppressWarnings("unchecked")
	@Override
	public Sequence<T> nextSequence() {
		
		if(sequenceParser == null) {
			if(useNormaliser) {
				if(normaliser == null) {
					throw new NullPointerException("Please set the Normaliser first");
				}
				
				sequenceParser = new SequenceParserImpl();
				
				Generalizer generalizer = new AdvancedGeneralizer(normaliser);
				
				sequenceParser.setGeneralizer(generalizer);
			}
			else {
				sequenceParser = new SequenceParserImpl();
			}
		}

		List<String []> patientStates = new ArrayList<String []>();

		if(previousLine != null) {
			patientStates.add(previousLine);
		}

		while (hasMoreSequences()) {
			String [] nextLine = currentIterator.next();

			int currentId = Integer.parseInt(nextLine[Constants.PATIENTNUMBER_COLUMN]);
			if(currentId != previousId) {
				Sequence<StateImpl> currentSequence;
				try {
					currentSequence = sequenceParser.getSequence(patientStates);
				} catch (ParseException | IOException | InterruptedException e) {
					logger.error(e.toString());
					return null;
				}

				previousId = currentId;
				previousLine = nextLine;

				return (Sequence<T>) currentSequence;
			}

			patientStates.add(nextLine);

		}

		Sequence<StateImpl> currentSequence;
		try {
			currentSequence = sequenceParser.getSequence(patientStates);
		} catch (ParseException | IOException | InterruptedException e) {
			logger.error(e.toString());
			return null;
		}

		return (Sequence<T>) currentSequence;
	}

	@Override
	public void reset() {
		try {
			this.currentIterator = new CsvIterator(underlyingIterable);
		} catch (IOException | InterruptedException e) {
			logger.error(e.toString());
		}
		currentIterator.next(); //ignore first line

		this.previousId = -1;
		this.previousLine = null;
	}


}
