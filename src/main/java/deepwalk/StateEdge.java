package deepwalk;

import org.deeplearning4j.graph.api.Edge;

public class StateEdge extends Edge<Integer> {

	public StateEdge(int from, int to, Integer value) {
		super(from, to, value, true);
	}

}
