package experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.canova.api.io.data.Text;
import org.canova.api.records.writer.impl.CSVRecordWriter;
import org.canova.api.writable.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import util.CsvIterator;

public class CreateTrainingTestData {

	private static CSVRecordWriter trainingWriter;
	private static CSVRecordWriter testWriter;
	private static CsvIterator iterator;
	
	protected static final Logger logger = LoggerFactory.getLogger(CreateTrainingTestData.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		
		logger.info("Started making datasets");
		
		File file = new File("conditions_sorted.csv");
		
		MedicalSequenceIterator<StateImpl> input = new MedicalSequenceIterator<StateImpl>(file, false);

		new TrainingDataGenerator(input, 0.80); 
		new TestingDataGenerator(input);
		
		List<Integer> lines = input.getTrainingData();
		
		iterator = new CsvIterator(file);
		trainingWriter = new CSVRecordWriter(new File("training2.csv"));
		testWriter = new CSVRecordWriter(new File("test2.csv"));
		
		int j = 0;
		int i = 0;
		while(iterator.hasNext()) {
			
			if(j % 100000 == 0) {
				logger.info("Iterations done: " + j);
			}
			
			List<Writable> writabels = new ArrayList<Writable>();
			String[] toWrite = iterator.next();
			for(String element: toWrite) {
				writabels.add(new Text(element));
			}
			
			if(j == lines.get(i)) {
				trainingWriter.write(writabels);
				i++;
			}
			else {
				testWriter.write(writabels);
			}
			

			j++;
		}
		

	}
	
	

}
