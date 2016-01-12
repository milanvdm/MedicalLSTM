package datahandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;
import util.Constants;

public class SequenceParserImpl implements SequenceParser {
	

	@Override
	public Sequence<StateImpl> getSequence(List<String []> states) {
		return convertListToSequence(states);
	}
	
	private Sequence<StateImpl> convertListToSequence(List<String []> states) {
		Sequence<StateImpl> sequence = new Sequence<StateImpl>();
		
		int i = 0;
		
		StateImpl state;
		while(i < states.size()) {
			if(i == 0) {
				state = getState(null, states.get(i));
			}
			
			else if(i == states.size() - 1) {
				break;
			}
			
			else {
				state = getState(states.get(i), states.get(i+1));
			}
			
			sequence.addElement(state);
			i++;
		}
		
		return sequence;
		
	}
	
	private StateImpl getState(String [] state1, String [] state2) {
		List<Double> vector = new ArrayList<Double>();
		
		if(state1 == null) {
			double timeDifference = 0;
			
			for(int toIgnore: Constants.COLUMS_TO_IGNORE) {
				state2 = (String []) ArrayUtils.remove(state2, toIgnore);
			}
			
			vector.add(timeDifference);
			
			for(String element: state2) {
				vector.add(Double.parseDouble(element));
			}
			
			return new StateImpl(vector);
		}
		
		double timeDifference = Double.parseDouble(state2[Constants.DAY_COLUMN]) - Double.parseDouble(state1[Constants.DAY_COLUMN]);
		
		for(int toIgnore: Constants.COLUMS_TO_IGNORE) {
			state2 = (String []) ArrayUtils.remove(state2, toIgnore);
		}
		
		vector.add(timeDifference);
		
		for(String element: state2) {
			vector.add(Double.parseDouble(element));
		}
		
		return new StateImpl(vector);
		
		
	}

}
