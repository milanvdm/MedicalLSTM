package tagcloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.canova.api.io.data.Text;
import org.canova.api.records.writer.impl.CSVRecordWriter;
import org.canova.api.writable.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.CsvIterator;

public class OMOPMapping {
	
	protected static final Logger logger = LoggerFactory.getLogger(OMOPMapping.class);

	private static String OMOP_FILE = "CONCEPT.csv";
	private static String ICD10_FILE = "care_icd10_en.csv";
	
	private static String OUTPUT_MAPPING = "mapping.csv";

	private static List<CodePair> omopData = new ArrayList<CodePair>();
	private static List<CodePair> icdData = new ArrayList<CodePair>();
	
	private static List<MatchedPair> matches = new ArrayList<MatchedPair>();
	
	// deleteCost, insertCost, replaceCost, swapCost
	// 2 * swapCost >= insertCost + deleteCost
	private static DamerauLevenshteinAlgorithm algo = new DamerauLevenshteinAlgorithm(1, 1, 1, 1);
	
	private static CSVRecordWriter csvWriter;
	

	public static void main(String[] args) throws IOException, InterruptedException {

		readOMOP();
		readICD10();
		
		matchCodes();
		
		makeCsvWriter();
		
		writeMatches();

	}

	private static void writeMatches() throws IOException {
		
		logger.info("Started writing");
		
		Collection<Writable> toWrite = new ArrayList<Writable>();
		
		toWrite.add(new Text("OMOP"));
		toWrite.add(new Text("DESC"));
		toWrite.add(new Text("ICD"));
		toWrite.add(new Text("DESC"));
		
		csvWriter.write(toWrite);
		
		toWrite.clear();
		
		for(MatchedPair pair: matches) {
			
			toWrite.add(new Text(pair.getOmopPair().getCode()));
			toWrite.add(new Text(pair.getOmopPair().getDescription()));
			toWrite.add(new Text(pair.getIcdPair().getCode()));
			toWrite.add(new Text(pair.getIcdPair().getDescription()));
			
			csvWriter.write(toWrite);
			
			toWrite.clear();
			
		}
		
	}
	
	private static void matchCodes() {
		
		logger.info("Started matching");
		String info1 = "Size OMOP: " + omopData.size();
		logger.info(info1);
		String info2 = "Size ICD: " + icdData.size();
		logger.info(info2);
		
		int count = 0;
		for(CodePair omopPair: omopData) {
			
			String description1 = omopPair.getDescription();
			
			CodePair bestMatch = null;
			int maxScore = 0;
			
			for(CodePair icdPair: icdData) {
				
				String description2 = icdPair.getDescription();
				
				int score = CountSameWords.countSameWords(description1, description2);
				
				if(score > maxScore) {
					maxScore = score;
					bestMatch = icdPair;
				}
				
				if(score == maxScore && bestMatch != null) {
					int currentScore = algo.execute(description1, bestMatch.getDescription());
					int newScore = algo.execute(description1, description2);
					
					if(currentScore > newScore) {
						bestMatch = icdPair;
					}
				}
			}
			
			if(maxScore == 0) {
				maxScore = Integer.MAX_VALUE;
				
				for(CodePair icdPair: icdData) {
					
					String description2 = icdPair.getDescription();
					
					int score = algo.execute(description1, description2);
					
					if(score < maxScore) {
						maxScore = score;
						bestMatch = icdPair;
					}
				}
			}
			
			count++;
			
			MatchedPair match = new MatchedPair(omopPair, bestMatch);
			matches.add(match);
			
			if(count % 5000 == 0) {
				String info = "OMOP amount done: " + count;
				logger.info(info);
			}
			
		}
		
	}

	private static void matchCodesDLA() {
		
		logger.info("Started matching");
		String info1 = "Size OMOP: " + omopData.size();
		logger.info(info1);
		String info2 = "Size ICD: " + icdData.size();
		logger.info(info2);
		
		int count = 0;
		for(CodePair omopPair: omopData) {
			
			String description1 = omopPair.getDescription();
			
			CodePair bestMatch = null;
			int maxScore = Integer.MAX_VALUE;
			
			for(CodePair icdPair: icdData) {
				
				String description2 = icdPair.getDescription();
				
				int score = algo.execute(description1, description2);
				
				if(score < maxScore) {
					maxScore = score;
					bestMatch = icdPair;
				}
			}
			
			count++;
			
			MatchedPair match = new MatchedPair(omopPair, bestMatch);
			matches.add(match);
			
			if(count % 5000 == 0) {
				String info = "OMOP amount done: " + count;
				logger.info(info);
			}
			
		}
		
	}

	private static void readOMOP() throws IOException, InterruptedException {

		File file = new File(OMOP_FILE);

		CsvIterator omopIterator = new CsvIterator(file, "\t");

		// [36358211, Protein bound iodine serum increased, Condition, MedDRA, LLT, C, 10037004, 19700101, 20991231, ;;]
		while(omopIterator.hasNext()) {
			String[] line = omopIterator.next();

			CodePair pair = new CodePair(line[0], line[1]);
			omopData.add(pair);
		}

	}

	private static void readICD10() throws IOException, InterruptedException {

		File file = new File(ICD10_FILE);

		CsvIterator icdIterator = new CsvIterator(file, ";");

		while(icdIterator.hasNext()) {
			String[] line = icdIterator.next();

			CodePair pair = new CodePair(line[0], line[1]);
			icdData.add(pair);
		}

	}
	
	private static void makeCsvWriter() throws FileNotFoundException {
		csvWriter = new CSVRecordWriter(new File(OUTPUT_MAPPING));

	}

}
