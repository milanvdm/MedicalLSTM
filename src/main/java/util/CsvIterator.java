package util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.canova.api.records.reader.impl.CSVRecordReader;
import org.canova.api.split.FileSplit;
import org.canova.api.writable.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvIterator implements Iterator<String[]> {
	
	private static final Logger logger = LoggerFactory.getLogger(CsvIterator.class);
	
	final private CSVRecordReader csvReader;
	
	private Collection<Writable> readData;
	private String[] data;
	
	public CsvIterator(File file) throws IOException, InterruptedException {
		this.csvReader = new CSVRecordReader(1, ",");
		FileSplit split = new FileSplit(file);

		csvReader.initialize(split);
		
		this.readData = csvReader.next();
		this.data = new String[readData.size()];
	}
	
	public CsvIterator(File file, String delimeter) throws IOException, InterruptedException {
		this.csvReader = new CSVRecordReader(1, delimeter);
		FileSplit split = new FileSplit(file);

		csvReader.initialize(split);
		
		this.readData = csvReader.next();
		this.data = new String[readData.size()];
	}

	
	
	private void convertCollection() {
		int i = 0;
		for(Writable toConvert: readData) {
			data[i] = toConvert.toString().replace("\"", "");
			i++;
		}
	}

	@Override
	public boolean hasNext() {
		if(data == null) {
			try {
				csvReader.close();
			} catch (IOException e) {
				return false;
			}
			return false;
		}

		return true;
	}

	@Override
	public String[] next() {
		convertCollection();
		String[] toReturn = data;

		try
		{
			readData = csvReader.next();
			data = new String[readData.size()];
		}
		catch ( Exception e )
		{
			data = null;
		}
		
		return toReturn;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
		
	}



}
