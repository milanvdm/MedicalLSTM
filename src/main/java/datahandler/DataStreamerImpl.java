package datahandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import com.opencsv.CSVReader;

import data.StateImpl;
import util.Constants;

public class DataStreamerImpl implements DataStreamer {
	
	private SequenceParser sequenceParser;
	
	public DataStreamerImpl() {
		this.sequenceParser = new SequenceParserImpl();
	}
	
	@Override
	public MedicalSequenceIterator<StateImpl> getMedicalIterator(File file) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(file), ';');
		
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
		
		return new MedicalSequenceIterator<>(sequences);
	}
	
	
}
