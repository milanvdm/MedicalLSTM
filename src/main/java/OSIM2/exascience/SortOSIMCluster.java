package OSIM2.exascience;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class SortOSIMCluster {

	public static String INPUT_CSV_EXA = "../OSIM/conditions_merged.csv";
	public static String OUTPUT_CSV_EXA = "../OSIM/conditions_sorted.csv";

	public static void main(String[] args) throws IOException {

		sortCsvFileOnColumn();

	}

	public static void sortCsvFileOnColumn() throws IOException {

		Comparator<List<String>> comparator = new Comparator<List<String>>() {
			@Override
			public int compare(List<String> r1, List<String> r2) {
				String[] line1 = r1.toArray(new String[r1.size()]);
				String[] line2 = r2.toArray(new String[r2.size()]);


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

		BufferedReader conditionFileReader = new BufferedReader(new FileReader(INPUT_CSV_EXA));

		CsvListReader conditionCsvReader = new CsvListReader(conditionFileReader, CsvPreference.STANDARD_PREFERENCE);

		List<List<String>> allLines = new ArrayList<List<String>>();

		List<String> read;
		while( (read = conditionCsvReader.read()) != null ) {
			allLines.add(read);
		}

		conditionCsvReader.close();

		Collections.sort(allLines, comparator);

		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(OUTPUT_CSV_EXA));
		CsvListWriter csvWriter = new CsvListWriter(fileWriter, CsvPreference.STANDARD_PREFERENCE);

		for(List<String> toWrite: allLines) {
			csvWriter.write(toWrite);
		}


		csvWriter.close();


	}


}


