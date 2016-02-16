package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.opencsv.CSVReader;

public class CsvIterator {
    
    public static Iterator<String []> getIterator(File file) throws IOException {
    	
    	final CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(file)), ',');
    	
    	return new Iterator<String []>()
        {
            String[] data = csvReader.readNext();

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

            @Override
            public String [] next()
            {
                String [] toReturn = data;

                try
                {
                    data = csvReader.readNext();
                }
                catch ( IOException e )
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
