package experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.canova.api.io.data.Text;
import org.canova.api.records.writer.impl.CSVRecordWriter;
import org.canova.api.writable.Writable;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import de.lmu.ifi.dbs.elki.datasource.ConcatenateFilesDatabaseConnection;
import util.Constants;
import util.CsvIterator;

public class CreateTrainingTestData {

	private static CSVRecordWriter trainingWriter;
	//private static CSVRecordWriter testWriter;
	private static CsvIterator iterator;

	protected static final Logger logger = LoggerFactory.getLogger(CreateTrainingTestData.class);

	public static void main(String[] args) throws IOException, InterruptedException {

		logger.info("Started making datasets");

		File file = new File("conditions_sorted.csv");
		//File file = new File(Constants.INPUT_CSV_TEST);

		MedicalSequenceIterator<StateImpl> input = new MedicalSequenceIterator<StateImpl>(file, false);

		new TrainingDataGenerator(input, 0.50); 
		new TestingDataGenerator(input);

		List<Integer> sequences = input.getTrainingData();

		logger.info("Started writing");

		iterator = new CsvIterator(file);
		trainingWriter = new CSVRecordWriter(new File("deepwalk.csv"));
		//testWriter = new CSVRecordWriter(new File("test.csv"));

		int currentSequenceCount = 0;
		int i = 0;
		while(input.hasMoreSequences()) {

			Sequence<StateImpl> sequence = input.nextSequence();
			int amountOfLines = sequence.getElements().size();

			if(currentSequenceCount % 100000 == 0) {
				logger.info("Sequences done: " + currentSequenceCount);
			}
			
			
			try {
				if(currentSequenceCount == sequences.get(i)) {
					
					int j = 0;
					while(j < amountOfLines) {
						
						List<Writable> writabels = new ArrayList<Writable>();
						String[] toWrite = iterator.next();
						for(String element: toWrite) {
							writabels.add(new Text(element));
						}
						
						trainingWriter.write(writabels);
						
						j++;
					}
					
					
					i++;
				}
				/*
				else {
					int j = 0;
					while(j < amountOfLines) {
						
						List<Writable> writabels = new ArrayList<Writable>();
						String[] toWrite = iterator.next();
						for(String element: toWrite) {
							writabels.add(new Text(element));
						}
						
						testWriter.write(writabels);
						
						j++;
					}
				}
				*/
			}
			catch(Exception e) {
				/*
				int j = 0;
				while(j < amountOfLines) {
					
					List<Writable> writabels = new ArrayList<Writable>();
					String[] toWrite = iterator.next();
					for(String element: toWrite) {
						writabels.add(new Text(element));
					}
					
					testWriter.write(writabels);
					
					j++;
				}
				*/
			}


			currentSequenceCount++;
		}


	}



}
