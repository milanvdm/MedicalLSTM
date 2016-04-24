package state2vec;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.clustering.sptree.DataPoint;
import org.deeplearning4j.clustering.vptree.VPTree;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import util.HelpFunctions;

public class KNNLookupTable<T extends SequenceElement> {
	
	private static final Logger logger = LoggerFactory.getLogger(KNNLookupTable.class);
	
	/**
	 * Use this class to feed in the data to the RNN!
	 */
	
	private final String similarityMeasure;
	
	private SequenceVectors<StateImpl> vectors;
	private int nearestNeighbours;
	private List<Double> weights;
	
	private INDArray columnMeans;
    private INDArray columnStds;
    
    private VPTree tree = null;;
    
    private int index = 0;

	
	public KNNLookupTable(SequenceVectors<StateImpl> vectors, int nearestNeighbours, String similarityMeasure) {
		this.vectors = vectors;
		this.nearestNeighbours = nearestNeighbours;
		this.weights = calculateWeights();
		calculateMeanStd();
		this.similarityMeasure = similarityMeasure;
	}
	
	private void calculateMeanStd() {
		INDArray wordLabels = null;
		
		boolean first = true;
		int rowNb = 0;
		for(String word: vectors.getVocab().words()) {
			
			double[] label = HelpFunctions.parse(word);
			
			if(first) {
				wordLabels = Nd4j.create(vectors.getVocab().numWords(), label.length);
				
				first = false;
			}
			
			wordLabels.putRow(rowNb, Nd4j.create(label));
			
			rowNb++;
		}
		
		this.columnMeans = wordLabels.mean(0);
	    this.columnStds = wordLabels.std(0).addi(Nd4j.scalar(Nd4j.EPS_THRESHOLD));
	   
		
	}
	
	private List<Double> calculateWeights() {
		List<Double> weights = new ArrayList<Double>();
		
		double i = nearestNeighbours;
		while(i != 0) {
			weights.add(i / nearestNeighbours);
			i--;
		}
		
		double sum = 0;
		for(double toSum: weights) {
			sum = sum + toSum;
		}
		
		List<Double> toReturn = new ArrayList<Double>();
		for(double weight: weights) {
			double newWeight = weight / sum;
			toReturn.add(newWeight);
		}
		
		return toReturn;
	}

	public boolean addSequenceElementVector(StateImpl sequenceElement) {
		
		String label = sequenceElement.getLabel();
		INDArray result = null;
		
		if(!vectors.hasWord(label)) {
			
			//logger.debug("Didn't find word in vocab!");
			
			List<DataPoint> kNearestNeighbours = nearestNeighbourLookup(sequenceElement); // KNN lookup 
			
			//logger.debug(Integer.toString(kNearestNeighbours.size()));
			
			List<INDArray> wordVectors = new ArrayList<INDArray>();
			for(DataPoint neighbour: kNearestNeighbours) {
				
				INDArray unNormalize = neighbour.getPoint();
				INDArray point = unNormalize.muli(columnStds).addi(columnMeans);
				
				List<Double> labelList = new ArrayList<Double>();
				int i = 0;
				while(i < point.columns()) {
					double toAdd = point.getDouble(i);
					labelList.add(toAdd);
					i++;
				}
				
				String neighbourLabel = labelList.toString();
				
				wordVectors.add(vectors.getWordVectorMatrix(neighbourLabel));
			}
			
			// gewogen gemiddelde van de arrays = 0.8 * array1 + 0.2 * array2
			int i = 0;
			while(i < wordVectors.size()) {
				if(result == null) {
					result = wordVectors.get(i).mul(weights.get(i));
				}
				else {
					result = result.add(wordVectors.get(i).mul(weights.get(i)));
				}
				
				i++;
			}
			
			if(result == null) {
				System.out.println(tree.getItems().size());
				System.out.println(sequenceElement.getState2vecLabelNormalized(columnMeans, columnStds));
				System.out.println(sequenceElement.getLabel());
				System.out.println(wordVectors.size());
				System.out.println(kNearestNeighbours.toString());
			}
			// word met vector in lookuptable steken!
			vectors.lookupTable().putVector(label, result);
			
			return true;
			
		}
		else {
			
			//logger.debug("Found word in vocab!");
			//result = vectors.getLookupTable().vector(label);
		}
		
		return false;
		
	}
	
	public SequenceVectors<StateImpl> getSequenceVectors() {
		return this.vectors;
	}

	private List<DataPoint> nearestNeighbourLookup(StateImpl label) {
		
		if(tree == null) { // Tree hasn't been build yet.
			
			List<DataPoint> points = new ArrayList<>();
			
			for(StateImpl state: vectors.getVocab().vocabWords()) {
				INDArray ndarray = state.getState2vecLabelNormalized(columnMeans, columnStds);
				
				DataPoint datapoint = new DataPoint(index, ndarray, similarityMeasure);
				
				points.add(datapoint);
				
				index++;
			}
			
	        tree = new VPTree(points);
	        
		}
		
        List<DataPoint> results = new ArrayList<>();
        
        //TODO: Add Datapoint to tree.
        DataPoint toSearch = new DataPoint(index, label.getState2vecLabelNormalized(columnMeans, columnStds), similarityMeasure);
        index++;
        
        tree.
        
        tree.search(toSearch, nearestNeighbours, results, new ArrayList<>());
        
        return results;
		
	}
	
	

}
