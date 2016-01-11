package data;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StateImpl extends SequenceElement implements State  {

	private static final long serialVersionUID = -5953078372301913975L;
	
	private List<Double> medicalVector = new ArrayList<Double>();
	
	
	public StateImpl(List<Double> input) {
		this.medicalVector = input;
	}
	
	@Override
	public List<Double> getMedicalVector() {
		return this.medicalVector;
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
