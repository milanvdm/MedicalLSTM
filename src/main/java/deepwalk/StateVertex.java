package deepwalk;

import java.util.List;

import org.deeplearning4j.graph.api.Vertex;

public class StateVertex extends Vertex<List<Double>> {

	public StateVertex(int idx, List<Double> value) {
		super(idx, value);
	}
	
	@Override
    public boolean equals(Object o) {
        if (!(o instanceof Vertex)) return false;
        Vertex<?> v = (Vertex<?>) o;
        if ((super.getValue() == null && v.getValue() != null) || (super.getValue() != null && v.getValue() == null)) return false;
        return super.getValue() == null || super.getValue().equals(v.getValue());
    }
	
	
}
