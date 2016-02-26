package datahandler.word2vec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.Drug;
import data.StateImpl;
import util.Constants;

public class SequenceParserImpl implements SequenceParser {


	@Override
	public Sequence<StateImpl> getSequence(List<String []> states) throws ParseException {
		return convertListToSequence(states);
	}

	private Sequence<StateImpl> convertListToSequence(List<String []> states) throws ParseException {
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
	private StateImpl getState(String[] state) throws ParseException {

		double timeDifference = 0;
		
		return getSingleState(state, timeDifference);

	}
	
	private StateImpl getState(String [] state1, String [] state2) throws ParseException {

		SimpleDateFormat parserSDF = new SimpleDateFormat("dd-MMM-yyyy");

		Date firstDate = parserSDF.parse(state1[Constants.DATE_COLUMN]);
		Date secondDate = parserSDF.parse(state2[Constants.DATE_COLUMN]);
		
		long startTime = firstDate.getTime();
		long endTime = secondDate.getTime();
		long diffTime = endTime - startTime;
		
		double timeDifference = diffTime / (1000 * 60 * 60 * 24);

		return getSingleState(state2, timeDifference);


	}
	
	private StateImpl getSingleState(String[] state, double timeDifference) throws ParseException {
		
		List<Object> completeState = new ArrayList<Object>();
		
		SimpleDateFormat parserSDF = new SimpleDateFormat("dd-MMM-yyyy");
		
		double conditionId = Double.parseDouble(state[Constants.CONDITION_ID_COLUMN]);
		Date date = parserSDF.parse(state[Constants.DATE_COLUMN]);
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
		completeState.add(getGeneralBirthYear(birthYear));
		completeState.add(genderConcept);
		completeState.add(conditionType);
		completeState.add(conditionTypeDesc);
		completeState.add(condition); //TODO: Get general condition
		completeState.add(drugs);

		completeState.add(getGeneralTimeDifference(timeDifference));
		completeState.add(decideSeason(date));
		
		return new StateImpl(completeState);
		
	}

	private Double getGeneralTimeDifference(double timeDifference) {
		if(timeDifference <= 0) {
			return 0.0;
		}
		else if(timeDifference <= 5) {
			return 1.0;
		}
		else if(timeDifference <= 10) {
			return 2.0;
		}
		else if(timeDifference <= 15) {
			return 3.0;
		}
		else if(timeDifference <= 20) {
			return 4.0;
		}
		else if(timeDifference <= 25) {
			return 5.0;
		}
		else {
			return 6.0;
		}
	}

	private Double getGeneralBirthYear(double birthYear) {
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
	
	private Double decideSeason(Date date) throws ParseException {
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

}
