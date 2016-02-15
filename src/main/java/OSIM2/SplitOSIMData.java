package OSIM2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.code.externalsorting.StringSizeEstimator;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class SplitOSIMData {


	private static long estimateAvailableMemory() {
		System.gc();
		return Runtime.getRuntime().freeMemory();
	}

	private static long estimateBestSizeOfBlocks(final long sizeoffile,
			final int maxtmpfiles, final long maxMemory) {
		// we don't want to open up much more than maxtmpfiles temporary
		// files, better run
		// out of memory first.
		long blocksize = sizeoffile / maxtmpfiles
				+ (sizeoffile % maxtmpfiles == 0 ? 0 : 1);

		// on the other hand, we don't want to create many temporary
		// files
		// for naught. If blocksize is smaller than half the free
		// memory, grow it.
		if (blocksize < maxMemory / 2) {
			blocksize = maxMemory / 2;
		}
		return blocksize;
	}
	
	private static CSVReader drugCsvReader;

	public static List<List<File>> splitConditions() throws NumberFormatException, IOException {
		File file = new File("/media/milan/Data/Thesis/Datasets/OSIM/Sorted/condition_era_sorted.csv");

		List<File> conditionFiles = new ArrayList<File>();
		List<File> drugFiles = new ArrayList<File>();
		List<File> personFiles = new ArrayList<File>();
		long blocksize = estimateBestSizeOfBlocks(file.length(),
				1024, estimateAvailableMemory());// in

		BufferedReader conditionFileReader = new BufferedReader(new FileReader("/media/milan/Data/Thesis/Datasets/OSIM/Sorted/condition_era_sorted.csv"));

		CSVReader conditionCsvReader = new CSVReaderBuilder(conditionFileReader).withSkipLines(0).build();

		BufferedReader drugFileReader = new BufferedReader(new FileReader("/media/milan/Data/Thesis/Datasets/OSIM/Merged/drug_era_merged.csv"));

		drugCsvReader = new CSVReaderBuilder(drugFileReader).withSkipLines(1).build();
		
		BufferedReader personFileReader = new BufferedReader(new FileReader("/media/milan/Data/Thesis/Datasets/OSIM/Sorted/person_sorted.csv"));

		CSVReader personCsvReader = new CSVReaderBuilder(personFileReader).withSkipLines(0).build();


		String[] nextLine;
		int currentId = -1;
		long currentblocksize = 0;
		List<String[]> tmplist = new ArrayList<String[]>();
		boolean write = false;

		while ((nextLine = conditionCsvReader.readNext()) != null) {

			if(currentblocksize > blocksize) {
				write = true;
			}

			int personId = Integer.parseInt(nextLine[2]);
			if(personId != currentId) {
				if(write == true) {
					conditionFiles.add(writeListToCsv(tmplist));
					drugFiles.add(splitMergedDrugFile(currentId));
					personFiles.add(splitPersonFile(currentId, personCsvReader));

					write = false;
					currentblocksize = 0;
					tmplist.clear();

				}

				currentId = personId;
			}

			tmplist.add(nextLine);
			currentblocksize += StringSizeEstimator
					.estimatedSizeOf(Arrays.toString(nextLine));


		}

		conditionCsvReader.close();
		drugCsvReader.close();
		personCsvReader.close();

		List<List<File>> toReturn = new ArrayList<List<File>>();
		toReturn.add(conditionFiles);
		toReturn.add(drugFiles);
		toReturn.add(personFiles);

		return toReturn;

	}

	private static String[] savedLine = null;

	private static File splitMergedDrugFile(int id) throws IOException {



		boolean write = false;
		int currentId = -1;
		String[] nextLine;
		List<String[]> tmplist = new ArrayList<String[]>();

		if(savedLine != null) {
			System.out.println("Found savedLine: " + Arrays.toString(savedLine));
			tmplist.add(savedLine);
			savedLine = null;
		}
		
		int i = 0;

		while ((nextLine = drugCsvReader.readNext()) != null) {

			int personId = Integer.parseInt(nextLine[3]);
			if(personId != currentId) {
				
				if(personId > id) {
					System.out.println("No Found id: " + id);
					write = true;
				}
				
				if(write == true) {
					savedLine = nextLine;
					System.out.println("Reached end of DrugSplitFile");
					break;
				}

				if(personId == id) {
					System.out.println("Found id: " + id);
					write = true;
				}
				
				

				currentId = personId;
			}
			
			if(i > 200000) {
				System.out.println("Already 200000 loops");
				i = 0;
			}
			
			tmplist.add(nextLine);
			i++;

		}

		File directory = new File("/media/milan/Data/Thesis/Datasets/OSIM/Splitted");

		File newtmpfile = File.createTempFile("splitMergedDrug",
				"flatfile", directory);

		OutputStream out = new FileOutputStream(newtmpfile);

		BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
				out, Charset.defaultCharset()));

		CSVWriter csvWriter = new CSVWriter(fbw);

		csvWriter.writeAll(tmplist);

		csvWriter.close();

		return newtmpfile;

	}

	public static List<List<File>> splitDrugs() throws NumberFormatException, IOException {

		File file = new File("/home/milan/workspace/MedicalLSTM/Dataset/Sorted/drug_era_sorted.csv");

		List<File> drugFiles = new ArrayList<File>();
		List<File> personFiles = new ArrayList<File>();
		long blocksize = estimateBestSizeOfBlocks(file.length(),
				1024, estimateAvailableMemory());// in

		BufferedReader drugFileReader = new BufferedReader(new FileReader("/home/milan/workspace/MedicalLSTM/Dataset/Sorted/drug_era_sorted.csv"));

		CSVReader drugCsvReader = new CSVReaderBuilder(drugFileReader).withSkipLines(0).build();

		BufferedReader personFileReader = new BufferedReader(new FileReader("/home/milan/workspace/MedicalLSTM/Dataset/Sorted/person_sorted.csv"));

		CSVReader personCsvReader = new CSVReaderBuilder(personFileReader).withSkipLines(0).build();


		String[] nextLine;
		int currentId = -1;
		long currentblocksize = 0;
		List<String[]> tmplist = new ArrayList<String[]>();
		boolean write = false;

		while ((nextLine = drugCsvReader.readNext()) != null) {

			if(currentblocksize > blocksize) {
				write = true;
			}

			int personId = Integer.parseInt(nextLine[3]);
			if(personId != currentId) {
				if(write == true) {
					drugFiles.add(writeListToCsv(tmplist));
					personFiles.add(splitPersonFile(currentId, personCsvReader));

					write = false;
					currentblocksize = 0;
					tmplist.clear();

				}

				currentId = personId;
			}

			tmplist.add(nextLine);
			currentblocksize += StringSizeEstimator
					.estimatedSizeOf(Arrays.toString(nextLine));


		}

		personCsvReader.close();
		drugCsvReader.close();

		List<List<File>> toReturn = new ArrayList<List<File>>();
		toReturn.add(drugFiles);
		toReturn.add(personFiles);

		return toReturn;

	}

	private static File splitPersonFile(int id, CSVReader personCsvReader) throws IOException {


		String[] nextLine;
		List<String[]> tmplist = new ArrayList<String[]>();

		while ((nextLine = personCsvReader.readNext()) != null) {

			tmplist.add(nextLine);

			int personId = Integer.parseInt(nextLine[0]);
			if(personId == id) {
				break;
			}

		}

		File directory = new File("/media/milan/Data/Thesis/Datasets/OSIM/Splitted");

		File newtmpfile = File.createTempFile("splitPerson",
				"flatfile", directory);

		OutputStream out = new FileOutputStream(newtmpfile);

		BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
				out, Charset.defaultCharset()));

		CSVWriter csvWriter = new CSVWriter(fbw);

		csvWriter.writeAll(tmplist);

		csvWriter.close();

		return newtmpfile;



	}



	private static File writeListToCsv(List<String[]> tmplist) throws IOException {
		File directory = new File("/media/milan/Data/Thesis/Datasets/OSIM/Splitted");

		File newtmpfile = File.createTempFile("splitCondtion",
				"flatfile", directory);

		OutputStream out = new FileOutputStream(newtmpfile);

		BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
				out, Charset.defaultCharset()));

		CSVWriter csvWriter = new CSVWriter(fbw);

		csvWriter.writeAll(tmplist);

		csvWriter.close();

		return newtmpfile;

	}

}
