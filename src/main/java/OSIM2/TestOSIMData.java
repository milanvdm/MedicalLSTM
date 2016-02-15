package OSIM2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class TestOSIMData {

	public static void main(String[] args) throws IOException, ParseException {

		File file = new File("/media/milan/Data/Thesis/Datasets/OSIM/Final/conditions_merged.csv");
		
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter("/media/milan/Data/Thesis/Datasets/OSIM/Final/test_conditions_merged.csv"));
		CSVWriter csvWriter = new CSVWriter(fileWriter);
		
		CSVReader conditionCsvReader = new CSVReaderBuilder(new FileReader(file)).withSkipLines(1).build();
		
		String[] nextLine;
		
		int i = 0;
		while ((nextLine = conditionCsvReader.readNext()) != null && i < 100000) {

			csvWriter.writeNext(nextLine);
			
			
			i++;


		}
		
		csvWriter.close();

	}
	
	
}
