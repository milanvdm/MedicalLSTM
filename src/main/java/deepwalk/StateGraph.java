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

import data.StateImpl;

public class StateGraph extends BaseGraph<StateImpl, Integer> {

	private List<Edge<Integer>>[] edges;  //edge[i].get(j).to = k, then edge from i -> k
	
	private List<Vertex<StateImpl>> vertices;
	
	public void removeZeroDegrees() {
		int i = 0;
		while(i < vertices.size()) {
			if(getVertexDegree(i) == 0) {
				edges[i] = new ArrayList<>();
				edges[i].add(new Edge<Integer>(i, vertices.size() - 1, 1, true));
			}
			
			i++;
		
		}
		
		edges[vertices.size() - 1] = new ArrayList<>();
		edges[vertices.size() - 1].add(new Edge<Integer>(vertices.size() - 1, vertices.size() - 1, 1, true));
	}


	@SuppressWarnings("unchecked")
	public StateGraph(int amountOfVertices){
		this.vertices = new ArrayList<>();
		this.edges = (List<Edge<Integer>>[]) Array.newInstance(List.class, amountOfVertices);
	}

	public void addVertex(Vertex<StateImpl> vertex) {
		if(vertices.contains(vertex)) {
			return;
		}
		else {
			this.vertices.add(vertex);
		}
	}


	@Override
	public void addEdge(Edge<Integer> edge) {
		
		List<Edge<Integer>> fromList = edges[edge.getFrom()];
        if(fromList == null){
            fromList = new ArrayList<>();
            edges[edge.getFrom()] = fromList;
        }
		
		edges[edge.getFrom()].add(edge);

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
	public List<Vertex<StateImpl>> getConnectedVertices(int vertex) {
		if(vertex < 0 || vertex >= vertices.size()) throw new IllegalArgumentException("Invalid vertex index: " + vertex);

        if(edges[vertex] == null) return Collections.emptyList();
        List<Vertex<StateImpl>> list = new ArrayList<>(edges[vertex].size());
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
	public Vertex<StateImpl> getRandomConnectedVertex(int vertex, Random rng) throws NoEdgesException {
		if(vertex < 0 || vertex >= vertices.size() ) throw new IllegalArgumentException("Invalid vertex index: " + vertex);
        if(edges[vertex] == null || edges[vertex].size() == 0)
            throw new NoEdgesException("Cannot generate random connected vertex: vertex " + vertex + " has no outgoing/undirected edges");
        int connectedVertexNum = rng.nextInt(edges[vertex].size());
        Edge<Integer> edge = edges[vertex].get(connectedVertexNum);
        if(edge.getFrom() == vertex ) return vertices.get(edge.getTo());    //directed or undirected, vertex -> x
        else return vertices.get(edge.getFrom());   //Undirected edge, x -> vertex
	}

	@Override
	public Vertex<StateImpl> getVertex(int idx) {
		if(idx < 0 || idx >= vertices.size() ) throw new IllegalArgumentException("Invalid index: " + idx);
		return vertices.get(idx);
	}

	@Override
	public int getVertexDegree(int vertex) {
		if(edges[vertex] == null) return 0;
        return edges[vertex].size();
	}

	@Override
	public List<Vertex<StateImpl>> getVertices(int[] indexes) {
		List<Vertex<StateImpl>> out = new ArrayList<>(indexes.length);
		for(int i : indexes) out.add(getVertex(i));
		return out;
	}

	@Override
	public List<Vertex<StateImpl>> getVertices(int from, int to) {
		if(to < from || from < 0 || to >= vertices.size())
			throw new IllegalArgumentException("Invalid range: from="+from + ", to="+to);
		List<Vertex<StateImpl>> out = new ArrayList<>(to-from+1);
		for(int i=from; i<=to; i++ ) out.add(getVertex(i));
		return out;
	}

	@Override
	public int numVertices() {
		return vertices.size();
	}
	
	public List<Vertex<StateImpl>> getAllVertices() {
		return vertices;
	}



}
