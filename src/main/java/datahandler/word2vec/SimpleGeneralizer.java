package datahandler.word2vec;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.CsvIterator;
import util.HelpFunctions;

public class SimpleGeneralizer implements Generalizer {

	private static Map<String, String> matches = null;
	private static List<String> icd = null;

	private static final String MAPPING_PATH = "/media/milan/Data/Thesis/Datasets/TagCloud/mapping.csv";
	private static final String ICD_PATH = "/media/milan/Data/Thesis/Datasets/TagCloud/care_icd10_en.csv";


	public Double getGeneralTimeDifference(double timeDifference) {
		if(timeDifference <= 0) {
			return 0.0;
		}
		else if(timeDifference <= 10) {
			return 1.0;
		}
		else if(timeDifference <= 20) {
			return 2.0;
		}
		else {
			return 3.0;
		}
	}

	public Double getGeneralBirthYear(double birthYear) {
		if(birthYear <= 1900) {
			return 0.0;
		}
		else if(birthYear <= 1910) {
			return 1.0;
		}
		else if(birthYear <= 1920) {
			return 2.0;
		}
		else if(birthYear <= 1930) {
			return 3.0;
		}
		else if(birthYear <= 1940) {
			return 4.0;
		}
		else if(birthYear <= 1950) {
			return 5.0;
		}
		else if(birthYear <= 1960) {
			return 6.0;
		}
		else if(birthYear <= 1970) {
			return 7.0;
		}
		else if(birthYear <= 1980) {
			return 8.0;
		}
		else if(birthYear <= 1990) {
			return 9.0;
		}
		else if(birthYear <= 2000) {
			return 10.0;
		}
		else if(birthYear <= 2010) {
			return 11.0;
		}
		else if(birthYear <= 2020) {
			return 12.0;
		}
		else {
			return 13.0;
		}
	}

	public Double decideSeason(Date date) throws ParseException {
		SimpleDateFormat f=new SimpleDateFormat("MM-dd");

		if(date.after(f.parse("04-21")) && date.before(f.parse("06-21"))){
			return 4.0;
		}
		else if(date.after(f.parse("06-20")) && (date.before(f.parse("09-23"))))
		{
			return 1.0;
		}
		else if(date.after(f.parse("09-22")) && date.before(f.parse("12-22")))
		{
			return 2.0;
		}
		else return 3.0;
	}

	public Double decideICDCategory(double condition) throws IOException, InterruptedException {
		if(matches == null || icd == null) {
			readMatches();
			getIcdTopLevelList();
		}

		String conditionString = String.format("%.0f", condition);

		String icd = getIcdTopLevel(matches.get(conditionString));

		return getIcdDouble(icd);


	}

	private Double getIcdDouble(String icd) {
		// A00.-
		String toConvert = icd.replace(".-", "");

		StringBuilder sb = new StringBuilder();
		for (char c : toConvert.toCharArray()) {
			sb.append((int) c);
		}

		Double toReturn = new Double(sb.toString());
		
		if(!HelpFunctions.icdDoubles.containsKey(toReturn)) {
			HelpFunctions.icdDoubles.put(toReturn, toConvert);
		}

		return toReturn;
	}

	private String getIcdTopLevel(String fullCode) {
		String toReturn = fullCode.substring(0, 3) + ".-";
		return toReturn;
	}

	private void getIcdTopLevelList() throws IOException, InterruptedException {
		CsvIterator iter = new CsvIterator(new File(ICD_PATH));

		icd = new ArrayList<String>();

		while(iter.hasNext()) {
			String[] line = iter.next();

			if(line[0].contains("-")) {
				icd.add(line[0]);
			}
		}

	}

	private void readMatches() throws IOException, InterruptedException {
		CsvIterator iter = new CsvIterator(new File(MAPPING_PATH));

		matches = new HashMap<String, String>();

		//OMOP,DESC,ICD,DESC
		while(iter.hasNext()) {
			String[] line = iter.next();

			matches.put(line[0], line[2]);
		}


	}

}
