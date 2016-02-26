package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class CsvIterator {
    
    public static Iterator<String []> getIterator(File file) throws IOException {
    	
    	final CsvListReader csvReader = new CsvListReader(new BufferedReader(new FileReader(file)), CsvPreference.STANDARD_PREFERENCE);
    	
    	return new Iterator<String []>()
        {
    		List<String> readData = csvReader.read();
            String[] data = readData.toArray(new String[readData.size()]);

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
                	List<String> readData = csvReader.read();
                    data = readData.toArray(new String[readData.size()]);
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
