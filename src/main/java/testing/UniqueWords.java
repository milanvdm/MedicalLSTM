package testing;

import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Comparator;
import java.util.Map.Entry;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import util.Constants;

public class UniqueWords {
	
	public static void main(String[] args) throws Exception {
		
		Map<String, Double> vocab = new HashMap<String, Double>();
		
		File file = new File(Constants.INPUT_CSV_TEST);
		int minimumFrequency = 5;
		
		MedicalSequenceIterator<StateImpl> sequenceIterator = new MedicalSequenceIterator<>(file);
		
		int count = 0;
		
		while(sequenceIterator.hasMoreSequences()) {
			Sequence<StateImpl> sequence = sequenceIterator.nextSequence();
			
			for(String element: sequence.asLabels()) {
				if(vocab.containsKey(element)) {
					vocab.put(element, vocab.get(element) + 1);
				}
				else {
					vocab.put(element, 1.0);
				}
				
				count++;
			}
		}
		
		int numberHigherFrequency = 0;
		
		for (Entry<String, Double> entry : vocab.entrySet())
        {
            if(entry.getValue() >= minimumFrequency) {
            	numberHigherFrequency = (int) (numberHigherFrequency + entry.getValue());
            }
        }
		
		vocab = sortByComparator(vocab, false);
		
		FileWriter writer = new FileWriter(Constants.OUTPUT_VOCAB); 
		
		double percentage = (double) numberHigherFrequency / count * 100;
		
		writer.write("Count: " + count + "\n");
		writer.write("Highter than minimum: " + numberHigherFrequency + "\n");
		writer.write("Percentage of data with higher frequency: " + percentage + "\n\n" );
		
		for (Map.Entry<String, Double> entry : vocab.entrySet())
		{
		    writer.write(entry.getKey() + " - " + entry.getValue() + "\n");
		}
		
		writer.flush();
		writer.close();
		
		
		
		
		
	}
	
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
    {

        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            public int compare(Entry<String, Double> o1,
                    Entry<String, Double> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	
	
	

}
