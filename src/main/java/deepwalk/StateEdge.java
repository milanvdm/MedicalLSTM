package deepwalk;

public class StateEdge {
	
	private int from;
	private int to;
	private int weight;

	public StateEdge(int from, int to, Integer weight) {
		this.from = from;
		this.to = to;
		this.weight = weight;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StateEdge)) return false;
		StateEdge e = (StateEdge) o;
        if ((this.from != e.getFrom()) || (this.to != e.getTo())) return false;
        return this.from == e.getFrom() && this.to == e.getTo();
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public void increaseWeight() {
		this.weight++;
		
	}

}
