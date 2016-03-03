package datahandler.word2vec;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.canova.api.io.data.Text;
import org.canova.api.records.reader.impl.CSVRecordReader;
import org.canova.api.records.writer.impl.CSVRecordWriter;
import org.canova.api.split.FileSplit;
import org.canova.api.writable.Writable;
import org.junit.Test;

import data.StateImpl;
import util.Constants;

public class MedicalSequenceIteratorTest {

	File file = new File(Constants.INPUT_CSV_TEST);
	
	
	
	@Test
	public void testReset() throws IOException, InterruptedException 
	{
		MedicalSequenceIterator<StateImpl> sequenceIterator = new MedicalSequenceIterator<>(file);
		
		int firstAmountOfSequences = 0;
		
		while(sequenceIterator.hasMoreSequences()) {
			sequenceIterator.nextSequence();
			firstAmountOfSequences++;
		}
		
		sequenceIterator.reset();
		
		int secondAmountOfSequences = 0;
		
		while(sequenceIterator.hasMoreSequences()) {
			sequenceIterator.nextSequence();
			secondAmountOfSequences++;
		}
		
		assertEquals(firstAmountOfSequences, secondAmountOfSequences);
		
	}
	
	@Test
    public void testWrite() throws Exception {
        File tempFile = File.createTempFile("canova","writer");
        tempFile.deleteOnExit();

        CSVRecordWriter writer = new CSVRecordWriter(tempFile);

        List<Writable> collection = new ArrayList<>();
        collection.add(new Text("12"));
        collection.add(new Text("13"));
        collection.add(new Text("14"));

        writer.write(collection);

        CSVRecordReader reader = new CSVRecordReader(0);
        reader.initialize(new FileSplit(tempFile));
        int cnt = 0;
        while (reader.hasNext()) {
            List<Writable> line = new ArrayList<>(reader.next());
            assertEquals(3, line.size());

            assertEquals(12, line.get(0).toInt());
            assertEquals(13, line.get(1).toInt());
            assertEquals(14, line.get(2).toInt());
            cnt++;
        }
        assertEquals(1, cnt);
    }
	
}
