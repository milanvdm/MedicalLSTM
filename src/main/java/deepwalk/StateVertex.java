package deepwalk;

import java.util.List;

public class StateVertex {
	
	private int idx;
	private List<Double> value;

	public StateVertex(int idx, List<Double> value) {
		this.idx = idx;
		this.value = value;
	}
	
	public List<Double> getValue() {
		return this.value;
	}
	
	public int getIdx() {
		return this.idx;
	}
	
	public void setIdx(int idx) {
		this.idx = idx;
	}
	
	@Override
    public boolean equals(Object o) {
        if (!(o instanceof StateVertex)) return false;
        StateVertex v = (StateVertex) o;
        if ((this.value == null && v.getValue() != null) || (this.value != null && v.getValue() == null)) return false;
        return this.value == null || this.value.equals(v.getValue());
    }
	
	
}
