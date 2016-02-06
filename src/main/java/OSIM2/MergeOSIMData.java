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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.NotFoundException;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import util.HelpFunctions;

public class MergeOSIMData {

	public static void main(String[] args) throws IOException {

		List<List<File>> files = openSavedList();

		if(files == null) {
			files = SplitOSIMData.splitDrugs();

			saveListToFile(files);
		}
		
		combineConditionDrugType(files.get(0), files.get(1));
		//combineDrugExposurePerson(files.get(0), files.get(1));

	}


	private static List<List<File>> openSavedList() {
		try{
		    
			   FileInputStream fin = new FileInputStream("/home/milan/workspace/MedicalLSTM/Dataset/filenames.ser");
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
			   
			FileOutputStream fout = new FileOutputStream("/home/milan/workspace/MedicalLSTM/Dataset/filenames.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(files);
			oos.close();
			System.out.println("Saved");
			   
		   }catch(Exception ex){
			   ex.printStackTrace();
		   }
		
	}
	
	private static void combineConditionDrugType(List<File> drugFiles, List<File> personFiles)  {
		
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
