package datahandler.word2vec;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.Drug;
import data.StateImpl;
import util.Constants;

public class SequenceParserImpl implements SequenceParser {
	
	private static final Logger logger = LoggerFactory.getLogger(SequenceParserImpl.class);
	
	Generalizer generalizer = new SimpleGeneralizer();
 

	@Override
	public Sequence<StateImpl> getSequence(List<String []> states) throws ParseException, IOException, InterruptedException {
		return convertListToSequence(states);
	}

	private Sequence<StateImpl> convertListToSequence(List<String []> states) throws ParseException, IOException, InterruptedException {
		Sequence<StateImpl> sequence = new Sequence<StateImpl>();

		int i = 0;

		StateImpl state;
		while(i < states.size()) {
			if(i == 0) {
				state = getState(states.get(i));
			}

			else {
				state = getState(states.get(i-1), states.get(i));
			}

			sequence.addElement(state);
			i++;
		}

		return sequence;

	}

	// "1","09-dec-2003","1","1942","8532","65","Condition Era","35305814","[]"
	private StateImpl getState(String[] state) throws ParseException, IOException, InterruptedException {

		double timeDifference = 0;
		
		return getSingleState(state, timeDifference);

	}
	
	private StateImpl getState(String [] state1, String [] state2) throws ParseException, IOException, InterruptedException {

		SimpleDateFormat parserSDF = new SimpleDateFormat("dd-MMM-yyyy");

		Date firstDate = parserSDF.parse(state1[Constants.DATE_COLUMN]);
		Date secondDate = parserSDF.parse(state2[Constants.DATE_COLUMN]);
		
		long startTime = firstDate.getTime();
		long endTime = secondDate.getTime();
		long diffTime = endTime - startTime;
		
		double timeDifference = diffTime / (1000 * 60 * 60 * 24);

		return getSingleState(state2, timeDifference);


	}
	
	private StateImpl getSingleState(String[] state, double timeDifference) throws ParseException, IOException, InterruptedException {
		
		List<Object> completeState = new ArrayList<Object>();
		
		SimpleDateFormat parserSDF = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat parserSDFSeason = new SimpleDateFormat("dd-MMM");
		
		double conditionId = Double.parseDouble(state[Constants.CONDITION_ID_COLUMN]);
		Date date = parserSDF.parse(state[Constants.DATE_COLUMN]);
		Date seasonDate = parserSDFSeason.parse(state[Constants.DATE_COLUMN]);
		double patientNumber = Double.parseDouble(state[Constants.PATIENTNUMBER_COLUMN]);
		double birthYear = Double.parseDouble(state[Constants.BIRTH_YEAR_COLUMN]);
		double genderConcept = Double.parseDouble(state[Constants.GENDER_CONCEPT_COLUMN]);
		double conditionType = Double.parseDouble(state[Constants.CONDITION_TYPE_COLUMN]);
		String conditionTypeDesc = state[Constants.CONDITION_TYPE_DESC_COLUMN];
		double condition = Double.parseDouble(state[Constants.CONDITION_COLUMN]);
		List<Drug> drugs = parseDrugList(state[Constants.DRUG_LIST_COLUMN]);
		
		int drugListSize = drugs.size();
		if(drugListSize > Constants.MAX_DRUG_LIST_SIZE) {
			Constants.MAX_DRUG_LIST_SIZE = drugListSize;
		}

		completeState.add(conditionId);
		completeState.add(date);
		completeState.add(patientNumber);
		completeState.add(generalizer.getGeneralBirthYear(birthYear));
		completeState.add(genderConcept);
		completeState.add(conditionType);
		completeState.add(conditionTypeDesc);
		completeState.add(generalizer.decideICDCategory(condition)); 
		completeState.add(drugs);

		completeState.add(generalizer.getGeneralTimeDifference(timeDifference));
		completeState.add(generalizer.decideSeason(seasonDate));
		
		return new StateImpl(completeState);
		
	}

	

	//[[7;Drug Era - 30 day window;30;1549080;18] - [7;Drug Era - 30 day window;30;1797513;17]]
	private List<Drug> parseDrugList(String drugList) {
		List<Drug> toReturn = new ArrayList<Drug>();
		
		if(drugList.equals("[]")) {
			return toReturn;
		}
		
		String[] drugs = drugList.split("\\] - \\[");
		
		for(String drug: drugs) {
			String newString = drug.replaceAll("\\[", "").replaceAll("\\]", "");
			String[] elements = newString.split(";");
			
			Drug toAdd = new Drug(Double.parseDouble(elements[0]), elements[1], Double.parseDouble(elements[2]), Double.parseDouble(elements[3]), Double.parseDouble(elements[4]));
			toReturn.add(toAdd);
			
		}
		
		return toReturn;
		
	}
	
	

}
