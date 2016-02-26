package OSIM2.exascience;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import util.Constants;

public class SortOSIMCluster {
	
	public static String INPUT_CSV_EXA = "../OSIM/conditions_merged.csv";
	public static String OUTPUT_CSV_EXA = "/media/milan/Data/Thesis/Datasets/OSIM/Sorted/conditions_sorted.csv";
	
	public static void main(String[] args) throws IOException {

		sortCsvFileOnColumn();

	}
	
	public static void sortCsvFileOnColumn() throws IOException {

		Comparator<String[]> comparator = new Comparator<String[]>() {
			@Override
			public int compare(String[] r1, String[] r2) {
				String[] line1 = r1;
				String[] line2 = r2;
				
				
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

		BufferedReader conditionFileReader = new BufferedReader(new FileReader(Constants.INPUT_CSV_TEST));

		CSVReader conditionCsvReader = new CSVReaderBuilder(conditionFileReader).withSkipLines(0).build();
		
		List<String[]> allLines = conditionCsvReader.readAll();
		
		Collections.sort(allLines, comparator);
		
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(OUTPUT_CSV_EXA));
		CSVWriter csvWriter = new CSVWriter(fileWriter);
		
		csvWriter.writeAll(allLines);
		
		csvWriter.close();
		
		
	}


}


