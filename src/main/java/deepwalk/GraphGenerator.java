package deepwalk;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;

public class GraphGenerator {
	
	private MedicalSequenceIterator<StateImpl> iterator;
	
	private int idx = 0;
	
	List<StateVertex> vertices = new ArrayList<StateVertex>();
	List<StateEdge> edges = new ArrayList<StateEdge>();
	
	public GraphGenerator(MedicalSequenceIterator<StateImpl> iterator) {
		this.iterator = iterator; 
	}
	
	public StateGraph createGraph() {
		
		StateGraph graph = new StateGraph();
		
		while(iterator.hasMoreSequences()) {
			Sequence<StateImpl> sequence = iterator.nextSequence();
			
			int i = 0;
			while(i <  sequence.getSequenceLabels().size()) {
				
				StateImpl state = sequence.getSequenceLabels().get(i);
				
				StateVertex dummy = new StateVertex(-1, state.getState2vecLabel());
				
				if(graph.containsVertex(dummy)) {
					
				}
				else {
					StateVertex toAdd = new StateVertex(idx, state.getState2vecLabel());
					graph.addVertex(toAdd);
					
					idx++;
				}
				
				i++;
			}
			
			
		}
	}
	

}
