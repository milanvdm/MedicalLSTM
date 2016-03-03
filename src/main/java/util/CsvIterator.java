package util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.canova.api.records.reader.impl.CSVRecordReader;
import org.canova.api.split.FileSplit;
import org.canova.api.writable.Writable;

public class CsvIterator {

	public static Iterator<String []> getIterator(File file) throws IOException, InterruptedException {

		final CSVRecordReader csvReader = new CSVRecordReader(1, ",");
		FileSplit split = new FileSplit(file);

		csvReader.initialize(split);

		return new Iterator<String[]>()
		{
			Collection<Writable> readData = csvReader.next();

			String[] data = new String[readData.size()];
			

			@Override
			public boolean hasNext()
			{
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
			
			private void convertCollection() {
				int i = 0;
				for(Writable toConvert: readData) {
					data[i] = toConvert.toString();
					i++;
				}
			}

			@Override
			public String[] next()
			{
				convertCollection();
				String[] toReturn = data;

				try
				{
					Collection<Writable> readData = csvReader.next();
					data = new String[readData.size()];
				}
				catch ( Exception e )
				{
					data = null;
				}

				return toReturn;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}



}
