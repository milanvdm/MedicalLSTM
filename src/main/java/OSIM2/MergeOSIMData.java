package OSIM2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.NotFoundException;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import util.HelpFunctions;

public class MergeOSIMData {

	public static void main(String[] args) throws IOException, ParseException {

		List<List<File>> files = openSavedList();

		if(files == null) {
			files = SplitOSIMData.splitConditions();

			saveListToFile(files);
		}

		combineConditionDrugType(files.get(0), files.get(1), files.get(2));
		//combineDrugExposurePerson(files.get(0), files.get(1));

	}


	private static List<List<File>> openSavedList() {
		try{

			FileInputStream fin = new FileInputStream("/media/milan/Data/Thesis/Datasets/OSIM/Splitted/filenames.ser");
			ObjectInputStream ois = new ObjectInputStream(fin);
			List<List<File>> files = (List<List<File>>) ois.readObject();
			ois.close();

			return files;

		}catch(Exception ex){
			System.out.println("Splitting the data");
			return null;
		} 
	}


	private static void saveListToFile(List<List<File>> files) {
		try{

			FileOutputStream fout = new FileOutputStream("/media/milan/Data/Thesis/Datasets/OSIM/Splitted/filenames.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(files);
			oos.close();
			System.out.println("Saved");

		}catch(Exception ex){
			ex.printStackTrace();
		}

	}

	private static Map<String, String[]> conditionTypes = new HashMap<String, String[]>();

	private static void combineConditionDrugType(List<File> conditionFiles, List<File> drugsFiles, List<File> personFiles) throws IOException, ParseException  {
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter("/media/milan/Data/Thesis/Datasets/OSIM/Final/conditions_merged.csv"));
		CSVWriter csvWriter = new CSVWriter(fileWriter);

		makeConditionTypes();

		//"CONDITION_ERA_ID","CONDITION_ERA_START_DATE","PERSON_ID","CONFIDENCE"!!,"CONDITION_ERA_END_DATE"!!,"CONDITION_CONCEPT_ID","CONDITION_OCCURRENCE_TYPE","CONDITION_OCCURRENCE_COUNT"|
		//"CONDITION_OCCURRENCE_TYPE","CONDITION_OCCURRENCE_TYPE_DESC","PERSISTENCE_WINDOW"!!,"CONDITION_OCCURRENCE_POSITION"|
		//"DRUG_ERA_ID,DRUG_ERA_START_DATE,DRUG_ERA_END_DATE,PERSON_ID,YEAR_OF_BIRTH,GENDER_CONCEPT_ID,DRUG_EXPOSURE_TYPE,DRUG_EXPOSURE_TYPE_DESC,PERSISTENCE_WINDOW,DRUG_CONCEPT_ID,DRUG_EXPOSURE_COUNT"


		String[] attributeNames = "CONDITION_ERA_ID,CONDITION_ERA_START_DATE,PERSON_ID,YEAR_OF_BIRTH,GENDER_CONCEPT_ID,CONDITION_CONCEPT_ID,CONDITION_OCCURRENCE_TYPE,CONDITION_OCCURRENCE_TYPE_DESC,CONDITION_OCCURRENCE_POSITION,DRUG_ERA_LIST".split(",");
		csvWriter.writeNext(attributeNames);

		int i = 0;
		while(i < conditionFiles.size()) {
		//while(i < 1) {
			CSVReader conditionCsvReader = new CSVReaderBuilder(new FileReader(conditionFiles.get(i))).withSkipLines(0).build();
			CSVReader drugsCsvReader = new CSVReaderBuilder(new FileReader(drugsFiles.get(i))).withSkipLines(0).build();
			CSVReader personsCsvReader = new CSVReaderBuilder(new FileReader(personFiles.get(i))).withSkipLines(0).build();

			List<String[]> conditions = conditionCsvReader.readAll();
			List<String[]> drugs = drugsCsvReader.readAll();
			List<String[]> persons = personsCsvReader.readAll();

			Map<String,List<String[]>> drugMap = new HashMap<String,List<String[]>>();

			for (String[] drug : drugs) {
				if(drugMap.containsKey(drug[3])) {
					drugMap.get(drug[3]).add(drug);
				}
				else {
					drugMap.put(drug[3], new ArrayList<String[]>());
					drugMap.get(drug[3]).add(drug);
				}		
			}
			
			Map<String,String[]> personMap = new HashMap<String,String[]>();
			for (String[] person : persons) personMap.put(person[0],person);

			int j = 0;
			while(j < conditions.size()) {

				String[] line = conditions.get(j);

				String personId = line[2];
				String conditionTypeId = line[6];

				String[] conditionType = conditionTypes.get(conditionTypeId);
				
				String[] person = null;
				try {
					person = Arrays.copyOf(personMap.get(personId), 3);
				}
				catch(NullPointerException e) {
					throw new NullPointerException(personId);
				}

				String[] drugList = getDrugsForDate(line[1], drugMap.get(personId));

				String[] toAdd1 = Arrays.copyOf(line, 2);
				String[] toAdd2 = new String[1];
				toAdd2[0] = line[5];

				toAdd2[0] = toAdd2[0].replace("\"|", "");

				String[] newRow = HelpFunctions.concatAll(toAdd1, person, conditionType, toAdd2, drugList);

				csvWriter.writeNext(newRow);

				j++;
			}

			System.out.println("File done");

			i++;
		}

		csvWriter.close();
	}

	private static String[] getDrugsForDate(String start, List<String[]> drugs) throws ParseException {

		if(drugs == null) {
			String[] toReturn = new String[1];
			toReturn[0] = "[]";
			return toReturn;
		}

		SimpleDateFormat parserSDF = new SimpleDateFormat("dd-MMM-yyyy");

		String[] toReturn = new String[1];

		StringBuilder builder = new StringBuilder();

		builder.append("[");

		Date startDate = parserSDF.parse(start); // 12-jul-2004

		boolean first = true;
		for(String[] line: drugs) {

			Date drugStartDate = parserSDF.parse(line[1]);

			boolean between = startDate.after(drugStartDate);
			

			if(between) {
				//"DRUG_ERA_ID,DRUG_ERA_START_DATE,DRUG_ERA_END_DATE,PERSON_ID,YEAR_OF_BIRTH,GENDER_CONCEPT_ID,DRUG_EXPOSURE_TYPE,DRUG_EXPOSURE_TYPE_DESC,PERSISTENCE_WINDOW,DRUG_CONCEPT_ID,DRUG_EXPOSURE_COUNT"
				long startTime = drugStartDate.getTime();
				long endTime = startDate.getTime();
				long diffTime = endTime - startTime;
				long diffDays = diffTime / (1000 * 60 * 60 * 24);
				
				if(diffDays > 31) {
					continue;
				}

				
				String[] toAdd = Arrays.copyOfRange(line, 6, 10);

				if(first) {
					builder.append("[");
					boolean check = true;
					
					for(String s: toAdd) {
						if(check) {
							builder.append(s);
							check = false;
						}
						else {
							builder.append(";");
							builder.append(s);
						}

					}
					builder.append(";");
					builder.append(diffDays);
					builder.append("]");
					first = false;
				}
				else {
					builder.append(" - ");

					builder.append("[");
					boolean check = true;
					
					for(String s: toAdd) {
						if(check) {
							builder.append(s);
							check = false;
						}
						else {
							builder.append(";");
							builder.append(s);
						}
					}
					builder.append(";");
					builder.append(diffDays);
					builder.append("]");
				}
			}

		}

		builder.append("]");
		
		toReturn[0] = builder.toString();
		return toReturn;


	}


	private static void makeConditionTypes() throws IOException {
		FileReader conditionTypeFileReader = new FileReader("/media/milan/Data/Thesis/Datasets/OSIM/condition_occurrence_ref.csv");
		CSVReader conditionTypeCsvReader = new CSVReader(conditionTypeFileReader);

		List<String[]> temp = conditionTypeCsvReader.readAll();
		temp.remove(0);
		for(String[] line: temp) {
			line[3] = line[3].replace("\"|", "");

			String[] newLine1 = Arrays.copyOf(line, 2);
			String[] newLine2 = Arrays.copyOfRange(line, 3, 3);

			String[] newLine = HelpFunctions.concatAll(newLine1, newLine2);
			conditionTypes.put(newLine[0], newLine);

		}

		conditionTypeCsvReader.close();

	}












	private static void combineDrugExposurePerson(List<File> drugFiles, List<File> personFiles) throws IOException {

		BufferedWriter fileWriter = new BufferedWriter(new FileWriter("/home/milan/workspace/MedicalLSTM/Dataset/drug_era.csv"));
		CSVWriter csvWriter = new CSVWriter(fileWriter);

		FileReader exposureFileReader = new FileReader("/home/milan/workspace/MedicalLSTM/Dataset/Sorted/drug_exposure_type.csv");
		CSVReader exposureCsvReader = new CSVReader(exposureFileReader);

		List<String[]> exposures = exposureCsvReader.readAll();
		exposures.remove(0);
		int size = exposures.size();
		exposures.get(size - 1)[2] = exposures.get(size - 1)[2].replace("\"|", "");
		exposureCsvReader.close();

		String[] attributeNames = "DRUG_ERA_ID,DRUG_ERA_START_DATE,DRUG_ERA_END_DATE,PERSON_ID,YEAR_OF_BIRTH,GENDER_CONCEPT_ID,DRUG_EXPOSURE_TYPE,DRUG_EXPOSURE_TYPE_DESC,PERSISTENCE_WINDOW,DRUG_CONCEPT_ID,DRUG_EXPOSURE_COUNT".split(",");
		csvWriter.writeNext(attributeNames);

		int i = 0;
		while(i < drugFiles.size()) {
			CSVReader drugCsvReader = new CSVReaderBuilder(new FileReader(drugFiles.get(i))).withSkipLines(0).build();
			CSVReader personCsvReader = new CSVReaderBuilder(new FileReader(personFiles.get(i))).withSkipLines(0).build();

			List<String[]> drugs = drugCsvReader.readAll();
			List<String[]> persons = personCsvReader.readAll();

			Map<Integer,String[]> personMap = new HashMap<Integer,String[]>();
			for (String[] person : persons) personMap.put(Integer.parseInt(person[0]),person);

			int j = 0;
			while(j < drugs.size()) {

				String[] line = drugs.get(j);

				int personId = Integer.parseInt(line[3]);
				int exposureId = Integer.parseInt(line[4]);

				String[] exposure = getExposure(exposureId, exposures);
				String[] person = null;
				try {
					person = Arrays.copyOf(personMap.get(personId), 3);
				}
				catch(NullPointerException e) {
					throw new NullPointerException(Integer.toString(personId));
				}

				String[] toAdd1 = Arrays.copyOf(line, 3);
				String[] toAdd2 = Arrays.copyOfRange(line, 5, 7);

				toAdd2[1] = toAdd2[1].replace("\"|", "");

				String[] newRow = HelpFunctions.concatAll(toAdd1, person, exposure, toAdd2);

				csvWriter.writeNext(newRow);

				j++;
			}

			System.out.println("File done");

			i++;
		}

		csvWriter.close();
	}



	private static String[] getExposure(int id, List<String[]> exposures) {

		for(String[] exposure: exposures) {
			if(Integer.parseInt(exposure[0]) == id) {

				return exposure;
			}
		}

		throw new NotFoundException();

	}

}
