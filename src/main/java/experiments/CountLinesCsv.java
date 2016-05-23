package experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.canova.api.records.reader.impl.CSVRecordReader;
import org.canova.api.split.FileSplit;
import org.canova.api.writable.Writable;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.Drug;
import data.StateImpl;
import datahandler.word2vec.Generalizer;
import datahandler.word2vec.MedicalSequenceIterator;
import datahandler.word2vec.SimpleGeneralizer;
import util.Constants;


public class CountLinesCsv {

	protected static final Logger logger = LoggerFactory.getLogger(CountLinesCsv.class);

	public static Map<String, Integer> statsGeneralized = new HashMap<String, Integer>();
	public static Map<String, Integer> statsNormal = new HashMap<String, Integer>();

	public static void main(String[] args) throws IOException, InterruptedException {

		int totalCount = 0;

		File file = new File("conditions_sorted.csv");

		MedicalSequenceIterator<StateImpl> medIter = new MedicalSequenceIterator<StateImpl>(file, false);

		while(medIter.hasMoreSequences()) {

			Sequence<StateImpl> sequence = medIter.nextSequence();

			totalCount = totalCount + sequence.getElements().size();

			for(StateImpl state: sequence.getElements()) {
				String label = state.getLabel();

				if(statsGeneralized.containsKey(label)) {
					statsGeneralized.put(label, statsGeneralized.get(label) + 1);
				}
				else {
					statsGeneralized.put(label, 1);
				}
			}

		}

		statsGeneralized = sortByComparator(statsGeneralized, false);

		FileWriter writer = new FileWriter("vocab_general.txt"); 
		
		writer.write("==GENERAL==" + "\n");

		for (Map.Entry<String, Integer> entry : statsGeneralized.entrySet())
		{
			writer.write(entry.getKey() + " - " + entry.getValue() + "\n");
		}

		writer.flush();
		writer.close();

		medIter = new MedicalSequenceIterator<StateImpl>(file, false, false);

		while(medIter.hasMoreSequences()) {

			Sequence<StateImpl> sequence = medIter.nextSequence();

			for(StateImpl state: sequence.getElements()) {
				String label = state.getLabel();

				if(statsNormal.containsKey(label)) {
					statsNormal.put(label, statsNormal.get(label) + 1);
				}
				else {
					statsNormal.put(label, 1);
				}
			}

		}
		
		statsNormal = sortByComparator(statsNormal, false);

		writer = new FileWriter("vocab_normal.txt"); 
		
		writer.write("==NORMAL==" + "\n");

		for (Map.Entry<String, Integer> entry : statsNormal.entrySet())
		{
			writer.write(entry.getKey() + " - " + entry.getValue() + "\n");
		}

		writer.flush();
		writer.close();

	}

	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
	{

		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Integer>>()
		{
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2)
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
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}






}
