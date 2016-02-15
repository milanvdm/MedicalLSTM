package data;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import util.Constants;

public class StateImpl extends SequenceElement implements State  {

	private static final long serialVersionUID = -5953078372301913975L;
	
	private List<Object> completeState = new ArrayList<Object>();
	private List<Double> medicalVector = new ArrayList<Double>();
	
	
	public StateImpl(List<Object> input) {
		this.completeState = input;
		this.medicalVector = convertToLabel(completeState);
		this.setElementFrequency(1); //Workaround of bug in DL4J
	}
	
	
	private List<Double> convertToLabel(List<Object> completeState) {
		
		for(int toIgnore: Constants.COLUMS_TO_IGNORE) {
			completeState.remove(toIgnore);
		}
		
		List<Double> castedList = new ArrayList<Double>();
		
		for(Object toCast: completeState) {
			Double toAdd = (Double) toCast;
			castedList.add(toAdd);
		}
		
		List<Double> toReturn = new ArrayList<Double>(castedList);
		return toReturn;
	}

	@Override
	public List<Double> getMedicalVector() {
		return this.medicalVector;
	}
	
	@Override
	public List<Object> getCompleteState() {
		return this.completeState;
	}
	
	@Override
	public String getLabel() {
		return medicalVector.toString();
	}

	@Override
	public String toJSON() {
		final ObjectMapper mapper = new ObjectMapper();
		
	    try {
	    	
			return mapper.writeValueAsString(medicalVector);
			
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
        if (object == null) return false;
        if (!(object instanceof SequenceElement)) return false;
        
        return medicalVector.equals(object);
	}

	

}
