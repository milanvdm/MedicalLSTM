package datahandler.word2vec;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

import data.StateImpl;
import util.Constants;

public class DataStreamerImpl implements DataStreamer {
	
	protected static final Logger logger = LoggerFactory.getLogger(DataStreamerImpl.class);
	
	private SequenceParser sequenceParser;
	
	public DataStreamerImpl() {
		this.sequenceParser = new SequenceParserImpl();
	}
	
	@Override
	public MedicalSequenceIterator<StateImpl> getMedicalIterator(File file) throws IOException {
		logger.info("Getting data from " + file.getName());
		
		CSVReader reader = new CSVReader(new FileReader(file), ';');
		reader.readNext(); //ignore first line
		
		List<Sequence<StateImpl>> sequences = new ArrayList<Sequence<StateImpl>>();
		
		
		int previousId = -1;
		List<String []> patientStates = new ArrayList<String []>();
		
		String [] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			
			int currentId = Integer.parseInt(nextLine[Constants.PATIENTNUMBER_COLUMN]);
			if(currentId != previousId) {
				Sequence<StateImpl> currentSequence = sequenceParser.getSequence(patientStates);
				sequences.add(currentSequence);
				
				previousId = currentId;
				patientStates = new ArrayList<String []>();
			}
			
			patientStates.add(nextLine);
			
		}
		
		reader.close();
		
		logger.info("Some results:");
		
		for(int i = 0; i < 20; i++) {
			Sequence<StateImpl> sequence = sequences.get(i);
			logger.info(sequence.toString());
		}
		
		return new MedicalSequenceIterator<>(sequences);
	}
	
	
}
