package data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import util.Constants;
import util.HelpFunctions;

public class StateImpl extends SequenceElement implements State  {
	
	//TODO: Condition ID algemener

	private static final long serialVersionUID = -5953078372301913975L;
	
	private List<Object> completeState = new ArrayList<Object>();
	private List<Double> state2vecLabel = new ArrayList<Double>();
	
	
	public StateImpl(List<Object> input) {
		this.completeState = input;
		this.state2vecLabel = convertToLabel(completeState);
		this.setElementFrequency(1); //Workaround of bug in DL4J
	}
	
	
	public StateImpl(String word) {
		for(double element: HelpFunctions.parse(word)) {
			state2vecLabel.add(element);
		}
	}


	/**
	 * At the moment only the druglist needs to be appended.
	 * CAREFUL: If labels of word2vec is changed (more ignored, then we need to add more!)
	 */
	@Override
	public INDArray getLstmVector(INDArray baseVector) {
		
		INDArray toAppend = flattenDrugList();
		
		INDArray toReturn = Nd4j.hstack(baseVector, toAppend);
		
		return toReturn;
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
	
	//TODO: Check if correct!
	public double compareTo(double[] other, INDArray mean, INDArray std) {
		double[] arr1 = ArrayUtils.toPrimitive(state2vecLabel.toArray(new Double[0]));
		double[] arr2 = other;
		
		INDArray ndarray1 = Nd4j.create(arr1).subi(mean).divi(std);
		INDArray ndarray2 = Nd4j.create(arr2).subi(mean).divi(std);
		
		return ndarray1.squaredDistance(ndarray2);
		
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
