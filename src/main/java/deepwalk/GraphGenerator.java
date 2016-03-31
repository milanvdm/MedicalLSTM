package deepwalk;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.graph.api.Edge;
import org.deeplearning4j.graph.api.Vertex;
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

		createVerticesAndEdges();
		
		StateGraph graph = new StateGraph();

		for(StateVertex vertex: vertices) {
			Vertex<List<Double>> toAdd = new Vertex<List<Double>>(vertex.getIdx(), vertex.getValue());
			graph.addVertex(toAdd);
		}
		
		for(StateEdge edge: edges) {
			Edge<Integer> toAdd = new Edge<Integer>(edge.getFrom(), edge.getTo(), edge.getWeight(), true);
			
			graph.addEdge(toAdd);
		}
		
		return graph;



	}

	private void createVerticesAndEdges() {
		StateVertex previousVertex = null;

		while(iterator.hasMoreSequences()) {
			Sequence<StateImpl> sequence = iterator.nextSequence();

			int i = 0;
			while(i <  sequence.getSequenceLabels().size()) {

				StateImpl state = sequence.getSequenceLabels().get(i);

				StateVertex dummy = new StateVertex(-1, state.getState2vecLabel());

				if(!vertices.contains(dummy)) {
					dummy.setIdx(idx);
					vertices.add(dummy);

					if(previousVertex != null) {
						addEdge(previousVertex, dummy);
					}

					previousVertex = dummy;

					idx++;
				}

				i++;
			}

			previousVertex = null;


		}

	}

	private void addEdge(StateVertex previousVertex, StateVertex currentVertex) {
		int from = previousVertex.getIdx();
		int to = currentVertex.getIdx();

		StateEdge dummy = new StateEdge(from, to, 1);

		if(edges.contains(dummy)) {
			edges.get(edges.indexOf(dummy)).increaseWeight();
		}
		else {
			edges.add(dummy);
		}

	}


}
