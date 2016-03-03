package testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.canova.api.records.reader.impl.CSVRecordReader;
import org.canova.api.records.writer.impl.CSVRecordWriter;
import org.canova.api.split.FileSplit;
import org.canova.api.writable.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Constants;


public class CountLinesCsv {

	protected static final Logger logger = LoggerFactory.getLogger(CountLinesCsv.class);

	public static CSVRecordReader csvReader;

	public static void main(String[] args) throws IOException, InterruptedException {

		countLines();

	}

	private static void makeCsvReader() throws IOException, InterruptedException {
		csvReader = new CSVRecordReader(1, ",");

		FileSplit split = new FileSplit(new File(Constants.INPUT_CSV));
		csvReader.initialize(split);

	}

	public static void countLines() throws IOException, InterruptedException {

		int totalCount = 0;

		makeCsvReader();

		logger.info("Started reading");
		int count = 0;

		while(csvReader.hasNext()) {

			csvReader.next();


			count ++;
			totalCount++;

			if(count == 500000) {
				logger.info("Read: " + totalCount);
				count = 0;
			}

		}

		csvReader.close();







	}




}
