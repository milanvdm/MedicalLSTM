package deepwalk;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.graph.api.Edge;
import org.deeplearning4j.graph.api.Vertex;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;

public class GraphGenerator {

	private SequenceIterator<StateImpl> iterator;

	private List<StateVertex> vertices = new ArrayList<StateVertex>();
	private List<StateEdge> edges = new ArrayList<StateEdge>();

	public GraphGenerator(SequenceIterator<StateImpl> iterator) {
		this.iterator = iterator; 
	}

	public StateGraph createGraph() {

		createVerticesAndEdges();
		
		StateGraph graph = new StateGraph(vertices.size());


		for(StateVertex vertex: vertices) {
			Vertex<List<Double>> toAdd = new Vertex<List<Double>>(vertex.getIdx(), vertex.getValue());
			graph.addVertex(toAdd);
		}

		int total = 0;
		for(StateEdge edge: edges) {
			Edge<Integer> toAdd = new Edge<Integer>(edge.getFrom(), edge.getTo(), edge.getWeight(), true);

			graph.addEdge(toAdd);
			
			total = total + edge.getWeight();
		}
		
		System.out.println(vertices.size());
		System.out.println(total);
		
		int j = 0;
		int i = 0;
		while(i < vertices.size()) {
			if(graph.getVertexDegree(i) == 0) {
				j++;
			}
			
			i++;
		
		}
		
		System.out.println(j);


		return graph;



	}

	public int getHighestId() {
		return vertices.size() - 1;
	}

	private void createVerticesAndEdges() {
		StateVertex previousVertex = null;

		iterator.reset();

		while(iterator.hasMoreSequences()) {
			Sequence<StateImpl> sequence = iterator.nextSequence();

			int i = 0;
			while(i <  sequence.getElements().size()) {

				StateImpl state = sequence.getElements().get(i);

				StateVertex dummy = new StateVertex(-1, state.getState2vecLabel());
				
				int dummyId = vertices.indexOf(dummy);

				if(dummyId == -1) {
					dummy.setIdx(vertices.size());
					dummyId = vertices.size();
					vertices.add(dummy);

				}
				else {
					dummy.setIdx(dummyId);
				}
				
				if(previousVertex != null) {
					addEdge(previousVertex, vertices.get(dummyId));
				}

				previousVertex = dummy;

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
