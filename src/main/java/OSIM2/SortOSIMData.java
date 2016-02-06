package OSIM2;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;

import com.google.code.externalsorting.ExternalSort;
import com.opencsv.CSVParser;

public class SortOSIMData {
	
	public static void main(String[] args) throws IOException {

		sortCsvFileOnColumn();

	}
	
	public static void sortCsvFileOnColumn() throws IOException {

		final CSVParser parser = new CSVParser();

		Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String r1, String r2) {
				String[] line1 = null;
				String[] line2 = null;
				try {
					line1 = parser.parseLine(r1);
					line2 = parser.parseLine(r2);
				} catch (IOException e) {
					e.printStackTrace();
				}


				Integer id1 = Integer.parseInt(line1[2]);
				Integer id2 = Integer.parseInt(line2[2]);

				return id1.compareTo(id2);

			}
		};

		List<File> l = ExternalSort.sortInBatch(new File("/media/milan/Data/Thesis/Datasets/OSIM/condition_era.csv"), comparator, 1024, Charset.defaultCharset(), new File("/media/milan/Data/Thesis/Datasets/OSIM/Sorted"), false, 1, false);
		ExternalSort.mergeSortedFiles(l, new File("/media/milan/Data/Thesis/Datasets/OSIM/Sorted/condition_era_sorted.csv"), comparator);
	}

}
