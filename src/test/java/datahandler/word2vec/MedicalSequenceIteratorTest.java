package datahandler.word2vec;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import data.StateImpl;
import util.Constants;

public class MedicalSequenceIteratorTest {

	File file = new File(Constants.INPUT_CSV_TEST);
	
	
	
	@Test
	public void testReset() throws IOException 
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
	
}
