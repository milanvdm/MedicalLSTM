package data;

import java.util.List;

public interface State {
	
	//"CONDITION_ERA_ID","CONDITION_ERA_START_DATE","PERSON_ID","YEAR_OF_BIRTH","GENDER_CONCEPT_ID","CONDITION_CONCEPT_ID","CONDITION_OCCURRENCE_TYPE","CONDITION_OCCURRENCE_TYPE_DESC","CONDITION_OCCURRENCE_POSITION","DRUG_ERA_LIST"
	// "1","09-dec-2003","1","1942","8532","65","Condition Era","35305814","[]"
	// "181010622","31-jan-2004","1","1942","8532","65","Condition Era","500000301","[[7;Drug Era - 30 day window;30;1549080;18] - [7;Drug Era - 30 day window;30;1797513;17]]"

	public List<Double> getMedicalVector();
	
	public List<Object> getCompleteState();
	
}
