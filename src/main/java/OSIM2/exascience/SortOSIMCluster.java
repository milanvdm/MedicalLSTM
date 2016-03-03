package OSIM2.exascience;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.canova.api.io.data.Text;
import org.canova.api.records.reader.impl.CSVRecordReader;
import org.canova.api.records.writer.impl.CSVRecordWriter;
import org.canova.api.split.FileSplit;
import org.canova.api.writable.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Constants;


public class SortOSIMCluster {

	public static String INPUT_CSV_EXA = "../OSIM/conditions_merged.csv";
	public static String OUTPUT_CSV_EXA = "../OSIM/conditions_sorted.csv";

	protected static final Logger logger = LoggerFactory.getLogger(SortOSIMCluster.class);

	public static CSVRecordReader csvReader;
	public static CSVRecordWriter csvWriter;

	public static void main(String[] args) throws IOException, InterruptedException {

		sortCsvFileOnColumn();

	}

	private static void makeCsvReader() throws IOException, InterruptedException {
		csvReader = new CSVRecordReader(1, ",");

		FileSplit split = new FileSplit(new File(Constants.INPUT_CSV_EXA));
		//FileSplit split = new FileSplit(new File(Constants.INPUT_CSV_SORTED_TEST));
		
		csvReader.initialize(split);

	}

	public static void sortCsvFileOnColumn() throws IOException, InterruptedException {

		Comparator<byte[][]> comparator = new Comparator<byte[][]>() {
			@Override
			public int compare(byte[][] r1, byte[][] r2) {
				String[] line1 = new String[9];
				String[] line2 = new String[9];


				int i = 0;
				while(i < r1.length && i < r2.length) {

					line1[i] = new String(r1[i], Charset.forName("UTF-8")).replace("\"", "");
					line2[i] = new String(r2[i], Charset.forName("UTF-8")).replace("\"", "");

					i++;

				}


				SimpleDateFormat parserSDF = new SimpleDateFormat("dd-MMM-yyyy");

				try {
					Date date1 = parserSDF.parse(line1[1]);
					Date date2 = parserSDF.parse(line2[1]);

					Integer id1 = Integer.parseInt(line1[2]);
					Integer id2 = Integer.parseInt(line2[2]);

					int idComparison = id1.compareTo(id2);
					return idComparison == 0 ? date1.compareTo(date2) : idComparison;
				} catch (ParseException e) {
					e.printStackTrace();
				}

				return -2;


			}
		};


		int totalCount = 0;


		List<byte[][]> allLines = new ArrayList<byte[][]>();

		makeCsvReader();

		logger.info("Started reading");
		int count = 0;

		while(csvReader.hasNext()) {

			Collection<Writable> line = csvReader.next();

			byte[][] toAdd = new byte[9][];

			int i = 0;
			for(Writable toConvert: line) {
				toAdd[i] = toConvert.toString().getBytes(Charset.forName("UTF-8"));
				i++;
			}

			allLines.add(toAdd);

			count ++;
			totalCount++;

			if(count == 500000) {
				logger.info("Read: " + totalCount);
				count = 0;
			}

		}

		csvReader.close();

		logger.info("Started sorting");
		Collections.sort(allLines, comparator);

		logger.info("finished testing");
		
		
		makeCsvWriter();

		logger.info("Started writing");
		count = 0;
		for(byte[][] line: allLines) {

			Collection<Writable> toWrite = new ArrayList<Writable>();

			for(byte[] toConvert: line) {
				String buggy = new String(toConvert, Charset.forName("UTF-8"));
				toWrite.add(new Text(buggy));
			}

			csvWriter.write(toWrite);

			count ++;

			if(count == 500000) {
				logger.info("Wrote 500.000 lines");
				count = 0;
			}
		}



		csvWriter.close();


 





	}

	private static void makeCsvWriter() throws FileNotFoundException {
		csvWriter = new CSVRecordWriter(new File(Constants.OUTPUT_CSV_SORTED_TEST));
		//csvWriter = new CSVRecordWriter(new File(Constants.OUTPUT_CSV_SORTED_TEST));

	}


}


