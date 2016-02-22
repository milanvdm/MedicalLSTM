package data;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import util.Constants;

public class StateImpl extends SequenceElement implements State  {

	private static final long serialVersionUID = -5953078372301913975L;
	
	private List<Object> completeState = new ArrayList<Object>();
	private List<Double> state2vecLabel = new ArrayList<Double>();
	
	
	public StateImpl(List<Object> input) {
		this.completeState = input;
		this.state2vecLabel = convertToLabel(completeState);
		this.setElementFrequency(1); //Workaround of bug in DL4J
	}
	
	@Override
	public INDArray getCompleteVector(INDArray baseVector) {
		
		int length = baseVector.length();
		
		baseVector.S
	}
	
	private INDArray flattenDrugList() {
		List<Drug> drugList = (List<Drug>) completeState.get(Constants.DRUG_LIST_COLUMN);

		double[][] arr = new double[Constants.MAX_DRUG_LIST_SIZE][Constants.AMOUNT_DRUG_ATTRIBUTES];
		
		int i = 0;
		while(i < drugList.size()) {
			arr[i][0] = drugList.get(i).getDrugExposureType();
			arr[i][1] = drugList.get(i).getDaysAgo();
			arr[i][2] = drugList.get(i).getPersistenceWindow();
			arr[i][3] = drugList.get(i).getDrugId();
			
			i++;
			
		}
		while(i < Constants.MAX_DRUG_LIST_SIZE) {
			arr[i][0] = 0;
			arr[i][1] = 0;
			arr[i][2] = 0;
			arr[i][3] = 0;
			
			i++;
		}
		
		double[] ret = new double[arr.length * arr[0].length];
        int count = 0;
        for (int x = 0; x < arr.length; x++)
            for (int y = 0; y < arr[x].length; y++)
                ret[count++] = arr[x][y];
      
        return Nd4j.create(ret);
		
	}
	
	
	private List<Double> convertToLabel(List<Object> completeState) {
		List<Object> trimmedList = new ArrayList<Object>();
		
		for(Object toCheck: completeState) {
			if(!Constants.COLUMS_TO_IGNORE.contains(completeState.indexOf(toCheck))) {
				trimmedList.add(toCheck);
			}
		}
		
		List<Double> castedList = new ArrayList<Double>();
		
		for(Object toCast: trimmedList) {
			Double toAdd = (Double) toCast;
			castedList.add(toAdd);
		}
		
		List<Double> toReturn = new ArrayList<Double>(castedList);
		return toReturn;
	}

	@Override
	public List<Double> getState2vecLabel() {
		return this.state2vecLabel;
	}
	
	@Override
	public List<Object> getCompleteState() {
		return this.completeState;
	}
	
	@Override
	public String getLabel() {
		return state2vecLabel.toString();
	}

	@Override
	public String toJSON() {
		final ObjectMapper mapper = new ObjectMapper();
		
	    try {
	    	
			return mapper.writeValueAsString(state2vecLabel);
			
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
        if (object == null) return false;
        if (!(object instanceof SequenceElement)) return false;
        
        return state2vecLabel.equals(object);
	}


	


	

	

}
