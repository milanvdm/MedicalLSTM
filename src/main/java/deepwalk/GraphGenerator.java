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
			
			
			
		}
	}
	

}
