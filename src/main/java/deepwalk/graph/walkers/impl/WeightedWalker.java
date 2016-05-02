package deepwalk.graph.walkers.impl;

import org.deeplearning4j.graph.api.*;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import deepwalk.graph.enums.NoEdgeHandling;
import deepwalk.graph.enums.WalkDirection;
import deepwalk.graph.walkers.GraphWalker;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**Given a graph, iterate through random walks on that graph of a specified length.
 * Unlike {@link RandomWalkIterator}, the {@code WeightedRandomWalkIterator} uses the values associated with each edge
 * to determine probabilities. Weights on each edge need not be normalized.<br>
 * Because the edge values are used to determine the probabilities of selecting an edge, the {@code WeightedRandomWalkIterator}
 * can only be used on graphs with an edge type that extends the {@link java.lang.Number} class (i.e., Integer, Double, etc)<br>
 * Random walks are generated starting at every node in the graph exactly once, though the order of the starting nodes
 * is randomized.
 * @author Alex Black
 */
public class WeightedWalker<T extends SequenceElement> extends RandomWalker<T>  implements GraphWalker<T> {
	protected int walkLength = 5;
	protected NoEdgeHandling noEdgeHandling = NoEdgeHandling.EXCEPTION_ON_DISCONNECTED;
	protected IGraph<T, ?> sourceGraph;
	protected AtomicInteger position = new AtomicInteger(0);
	protected Random rng = new Random(System.currentTimeMillis());
	protected long seed;
	protected int[] order;
	protected WalkDirection walkDirection;
	protected double alpha;

	private static final Logger logger = LoggerFactory.getLogger(WeightedWalker.class);

	protected WeightedWalker() {

	}


	/**
	 * This method checks, if walker has any more sequences left in queue
	 *
	 * @return
	 */
	@Override
	public boolean hasNext() {
		return position.get() < sourceGraph.numVertices();
	}

	/**
	 * This method returns next walk sequence from this graph
	 *
	 * @return
	 */
	@Override
	public Sequence<T> next() {
		int[] visitedHops = new int[walkLength];
		Arrays.fill(visitedHops, -1);

		Sequence<T> sequence = new Sequence<T>();

		int startPosition = position.getAndIncrement();
		int lastId = -1;
		int startPoint = order[startPosition];
		//System.out.println("");


		startPosition = startPoint;

		//if (startPosition == 0 || startPoint % 1000 == 0)
		//   System.out.println("ATZ Walk: ");

		for (int i = 0; i < walkLength; i++) {
			Vertex<T> vertex = sourceGraph.getVertex(startPosition);

			int currentPosition = startPosition;

			sequence.addElement(vertex.getValue());
			visitedHops[i] = vertex.vertexID();
			//if (startPoint == 0 || startPoint % 1000 == 0)
			// System.out.print("" + vertex.vertexID() + " -> ");


			if (alpha > 0 && lastId != startPoint && lastId != -1 && alpha > rng.nextDouble()) {
				startPosition = startPoint;
				continue;
			}


			// get next vertex
			switch (walkDirection) {
			case RANDOM: {
				List<? extends Edge<?>> edgeList = sourceGraph.getEdgesOut(currentPosition);
				
				//To do a weighted random walk: we need to know total weight of all outgoing edges
	            double totalWeight = 0.0;
	            for (Edge<?> edge : edgeList) {
	                totalWeight += ((Integer) edge.getValue()).doubleValue();
	            }

	            double d = rng.nextDouble();
	            double threshold = d * totalWeight;
	            double sumWeight = 0.0;
	            for (Edge<?> edge : edgeList) {
	                sumWeight += ((Integer) edge.getValue()).doubleValue();
	                if (sumWeight >= threshold) {
	                    if (edge.isDirected()) {
	                        currentPosition = edge.getTo();
	                    } else {
	                        if (edge.getFrom() == currentPosition) {
	                        	currentPosition = edge.getTo();
	                        } else {
	                        	currentPosition = edge.getFrom(); //Undirected edge: might be next--currVertexIdx instead of currVertexIdx--next
	                        }
	                    }
	                    startPosition = currentPosition;
	                    break;
	                }
	            }

			};
			break;

			default:
				throw new UnsupportedOperationException("Unknown WalkDirection ["+ walkDirection +"]");
			}

			lastId = vertex.vertexID();
		}

		//if (startPoint == 0 || startPoint % 1000 == 0)
		//System.out.println("");
		return sequence;
	}

	/**
	 * This method resets walker
	 *
	 * @param shuffle if TRUE, order of walks will be shuffled
	 */
	@Override
	public void reset(boolean shuffle) {
		this.position.set(0);
		if (shuffle) {
			logger.debug("Calling shuffle() on entries...");
			// https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
			for(int i=order.length-1; i>0; i-- ){
				int j = rng.nextInt(i+1);
				int temp = order[j];
				order[j] = order[i];
				order[i] = temp;
			}
		}
	}

	public static class Builder<T extends SequenceElement> {
		protected int walkLength = 5;
		protected NoEdgeHandling noEdgeHandling = NoEdgeHandling.RESTART_ON_DISCONNECTED;
		protected IGraph<T, ?> sourceGraph;
		protected long seed = 0;
		protected WalkDirection walkDirection = WalkDirection.FORWARD_ONLY;
		protected double alpha;

		/**
		 * Builder constructor for RandomWalker
		 *
		 * @param graph source graph to be used for this walker
		 */
		public Builder(@NonNull IGraph<T, ?> graph) {
			this.sourceGraph = graph;
		}

		/**
		 * This method specifies output sequence (walk) length
		 *
		 * @param walkLength
		 * @return
		 */
		public Builder<T> setWalkLength(int walkLength) {
			this.walkLength = walkLength;
			return this;
		}

		/**
		 * This method defines walker behavior when it gets to node which has no next nodes available
		 * Default value: RESTART_ON_DISCONNECTED
		 *
		 * @param handling
		 * @return
		 */
		public Builder<T> setNoEdgeHandling(@NonNull NoEdgeHandling handling) {
			this.noEdgeHandling = handling;
			return this;
		}

		/**
		 * This method specifies random seed.
		 *
		 * @param seed
		 * @return
		 */
		public Builder<T> setSeed(long seed) {
			this.seed = seed;
			return this;
		}

		/**
		 * This method defines next hop selection within walk
		 *
		 * @param direction
		 * @return
		 */
		public Builder<T> setWalkDirection(@NonNull WalkDirection direction) {
			this.walkDirection = direction;
			return this;
		}

		/**
		 * This method defines a chance for walk restart
		 * Good value would be somewhere between 0.03-0.07
		 *
		 * @param alpha
		 * @return
		 */
		public Builder<T> setRestartProbability(double alpha) {
			this.alpha = alpha;
			return this;
		}

		/**
		 * This method builds RandomWalker instance
		 * @return
		 */
		public WeightedWalker<T> build() {
			WeightedWalker<T> walker = new WeightedWalker<T>();
			walker.noEdgeHandling = this.noEdgeHandling;
			walker.sourceGraph = this.sourceGraph;
			walker.walkLength = this.walkLength;
			walker.seed = this.seed;
			walker.walkDirection = this.walkDirection;
			walker.alpha = this.alpha;

			walker.order = new int[sourceGraph.numVertices()];
			for (int i =0; i <walker.order.length; i++) {
				walker.order[i] = i;
			}

			if (this.seed != 0)
				walker.rng = new Random(this.seed);

			return walker;
		}
	}
}