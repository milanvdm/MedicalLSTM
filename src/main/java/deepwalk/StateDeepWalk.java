package deepwalk;

import java.util.List;

import org.deeplearning4j.graph.models.deepwalk.DeepWalk;

public class StateDeepWalk {
	
	private DeepWalk<List<Double>, Integer> trainedVectors;

	public void trainDeepWalk(StateGraph graph, int windowSize, double lr, int vectorLength, int walkLength) {
		
		DeepWalk<List<Double>, Integer> deepwalk = new DeepWalk.Builder<List<Double>, Integer>()

				.learningRate(lr)

				.vectorSize(vectorLength)
				
				.windowSize(windowSize)

				.build();
		
		deepwalk.fit(graph, walkLength);
		
		this.trainedVectors = deepwalk;
		
		
	}
	
	public DeepWalk<List<Double>, Integer> getTrainedModel() {
		return trainedVectors;
	}

}
