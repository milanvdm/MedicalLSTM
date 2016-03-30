package deepwalk;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.deeplearning4j.graph.api.BaseGraph;
import org.deeplearning4j.graph.api.Edge;
import org.deeplearning4j.graph.api.Vertex;
import org.deeplearning4j.graph.exception.NoEdgesException;

public class StateGraph extends BaseGraph<List<Double>, Integer> {

	private List<Edge<Integer>>[] edges;  //edge[i].get(j).to = k, then edge from i -> k
	private List<Vertex<List<Double>>> vertices;


	@SuppressWarnings("unchecked")
	public StateGraph(){
		this.vertices = new ArrayList<>();
		this.edges = (List<Edge<Integer>>[]) Array.newInstance(List.class,vertices.size());
	}

	public void addVertex(StateVertex vertex) {
		this.vertices.add(vertex);
	}


	@Override
	public void addEdge(Edge<Integer> edge) {
		if(edge.getFrom() < 0 || edge.getTo() >= vertices.size() )
			throw new IllegalArgumentException("Invalid edge: " + edge + ", from/to indexes out of range");

		List<Edge<Integer>> fromList = edges[edge.getFrom()];
		if(fromList == null){
			fromList = new ArrayList<>();
			edges[edge.getFrom()] = fromList;
		}
		addEdgeHelper(edge,fromList);

		if(edge.isDirected()) return;

	}

	private void addEdgeHelper(Edge<Integer> edge, List<Edge<Integer>> list ){

		//Check for multiple edges
		boolean duplicate = false;
		Edge<Integer> toRemove = null;

		for(Edge<Integer> e : list ){
			if(e.getTo() == edge.getTo()){
				toRemove = e;
				duplicate = true;
				break;
			}
		}


		if(!duplicate){
			list.add(edge);
		}
		else {

			StateEdge newEdge = new StateEdge(toRemove.getFrom(), toRemove.getTo(), toRemove.getValue() + 1);
			list.remove(toRemove);
			list.add(newEdge);

		}

	}

	@Override
	public int[] getConnectedVertexIndices(int vertex) {
		int[] out = new int[(edges[vertex] == null ? 0 : edges[vertex].size())];
        if(out.length == 0 ) return out;
        for(int i=0; i<out.length; i++ ){
            Edge<Integer> e = edges[vertex].get(i);
            out[i] = (e.getFrom() == vertex ? e.getTo() : e.getFrom() );
        }
        return out;
	}

	@Override
	public List<Vertex<List<Double>>> getConnectedVertices(int vertex) {
		if(vertex < 0 || vertex >= vertices.size()) throw new IllegalArgumentException("Invalid vertex index: " + vertex);

        if(edges[vertex] == null) return Collections.emptyList();
        List<Vertex<List<Double>>> list = new ArrayList<>(edges[vertex].size());
        for(Edge<Integer> edge : edges[vertex]){
            list.add(vertices.get(edge.getTo()));
        }
        return list;
	}

	@Override
	public List<Edge<Integer>> getEdgesOut(int vertex) {
		if(edges[vertex] == null ) return Collections.emptyList();
        return new ArrayList<>(edges[vertex]);
	}

	@Override
	public Vertex<List<Double>> getRandomConnectedVertex(int vertex, Random rng) throws NoEdgesException {
		if(vertex < 0 || vertex >= vertices.size() ) throw new IllegalArgumentException("Invalid vertex index: " + vertex);
        if(edges[vertex] == null || edges[vertex].size() == 0)
            throw new NoEdgesException("Cannot generate random connected vertex: vertex " + vertex + " has no outgoing/undirected edges");
        int connectedVertexNum = rng.nextInt(edges[vertex].size());
        Edge<Integer> edge = edges[vertex].get(connectedVertexNum);
        if(edge.getFrom() == vertex ) return vertices.get(edge.getTo());    //directed or undirected, vertex -> x
        else return vertices.get(edge.getFrom());   //Undirected edge, x -> vertex
	}

	@Override
	public Vertex<List<Double>> getVertex(int idx) {
		if(idx < 0 || idx >= vertices.size() ) throw new IllegalArgumentException("Invalid index: " + idx);
		return vertices.get(idx);
	}

	@Override
	public int getVertexDegree(int vertex) {
		if(edges[vertex] == null) return 0;
        return edges[vertex].size();
	}

	@Override
	public List<Vertex<List<Double>>> getVertices(int[] indexes) {
		List<Vertex<List<Double>>> out = new ArrayList<>(indexes.length);
		for(int i : indexes) out.add(getVertex(i));
		return out;
	}

	@Override
	public List<Vertex<List<Double>>> getVertices(int from, int to) {
		if(to < from || from < 0 || to >= vertices.size())
			throw new IllegalArgumentException("Invalid range: from="+from + ", to="+to);
		List<Vertex<List<Double>>> out = new ArrayList<>(to-from+1);
		for(int i=from; i<=to; i++ ) out.add(getVertex(i));
		return out;
	}

	@Override
	public int numVertices() {
		return vertices.size();
	}



}
