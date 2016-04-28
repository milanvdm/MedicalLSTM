package deepwalk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deeplearning4j.graph.api.Edge;
import org.deeplearning4j.graph.api.Vertex;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;

public class GraphGenerator {

	private SequenceIterator<StateImpl> iterator;

	private List<StateVertex> vertices = new ArrayList<StateVertex>();
	private List<StateEdge> edges = new ArrayList<StateEdge>();
	
	protected static final Logger logger = LoggerFactory.getLogger(GraphGenerator.class);

	public GraphGenerator(SequenceIterator<StateImpl> iterator) {
		this.iterator = iterator; 
	}

	public StateGraph createGraph() {
		
		logger.info("Making Vertices and Edges from SequenceIterator");

		createVerticesAndEdges();
		
		logger.info("Making StateGraph");
		
		StateVertex dummy = new StateVertex(vertices.size(), new StateImpl(Arrays.asList(-1.0, -1.0, -1.0, -1.0, -1.0, -1.0).toString()));
		vertices.add(dummy);
		
		StateGraph graph = new StateGraph(vertices.size());


		for(StateVertex vertex: vertices) {
			Vertex<StateImpl> toAdd = new Vertex<StateImpl>(vertex.getIdx(), vertex.getValue());
			graph.addVertex(toAdd);
		}
		
		

		int total = 0;
		for(StateEdge edge: edges) {
			Edge<Integer> toAdd = new Edge<Integer>(edge.getFrom(), edge.getTo(), edge.getWeight(), true);

			graph.addEdge(toAdd);
			
			total = total + edge.getWeight();
		}
		
		graph.removeZeroDegrees();

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

			if(sequence.getElements().size() == 1) {
				continue;
			}
			
			int i = 0;
			while(i <  sequence.getElements().size()) {

				StateImpl state = sequence.getElements().get(i);

				StateVertex dummy = new StateVertex(-1, state);
				
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
