package deepwalk;

import java.util.List;

import org.deeplearning4j.graph.api.Vertex;

public class StateVertex extends Vertex<List<Double>> {

	public StateVertex(int idx, List<Double> value) {
		super(idx, value);
	}
	
	
}
