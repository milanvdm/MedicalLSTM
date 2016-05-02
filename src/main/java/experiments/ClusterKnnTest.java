package experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import state2vec.KDTree.SearchResult;
import state2vec.KNNLookupTable;
import util.CsvIterator;
import util.HelpFunctions;

public class ClusterKnnTest {

	protected static final Logger logger = LoggerFactory.getLogger(ClusterKnnTest.class);

	public Map<String, Set<Double>> clusters = new HashMap<String, Set<Double>>();

	public ClusterKnnTest() throws IOException, InterruptedException {
		readClusters();
	}

	private void readClusters() throws IOException, InterruptedException {
		//File file = new File("clusters/clusters.csv");
		File file = new File("clusters.csv");

		//skip first 5 lines
		CsvIterator iterator = new CsvIterator(file);

		iterator.next();
		iterator.next();
		iterator.next();
		iterator.next();
		iterator.next();

		while(iterator.hasNext()) {
			String[] line = iterator.next();

			String clusterName = line[0];
			String diag1 = line[3];
			String diag2 = line[5];
			String diag3 = line[9];
			String diag4 = line[13];

			if(clusters.containsKey(clusterName)) {

				Set<Double> diags = clusters.get(clusterName);

				diags.add(stringToDouble(diag1));
				diags.add(stringToDouble(diag2));
				diags.add(stringToDouble(diag3));
				diags.add(stringToDouble(diag4));
			}
			else {

				clusters.put(clusterName, new HashSet<Double>());

				Set<Double> diags = clusters.get(clusterName);

				diags.add(stringToDouble(diag1));
				diags.add(stringToDouble(diag2));
				diags.add(stringToDouble(diag3));
				diags.add(stringToDouble(diag4));
			}
		}

		//logger.info(clusters.toString());
		//String info = "" + clusters.size();
		//logger.info(info);


	}

	public void checkClusters1(KNNLookupTable<StateImpl> lookupTable, Map<String, INDArray> newLabels, int k, ResultWriter writer) throws Exception {

		writer.writeLine("==CLUSTERTEST 1==");
		writer.writeLine("k: " + k);
		writer.writeLine("");

		//String info = "" + table.getVocabCache().numWords();
		//logger.info(info);

		Map<Double, List<Double>> icdCluster = new HashMap<Double, List<Double>>();

		for (Map.Entry<String, INDArray> labelPair : newLabels.entrySet()) {
			Double icd10 = HelpFunctions.parse(labelPair.getKey())[3];

			Set<Double> otherDiags = new HashSet<Double>();

			boolean toContinue = false;
			for (Map.Entry<String, Set<Double>> entry : clusters.entrySet())
			{
				if(entry.getValue().contains(icd10)) {
					if(!entry.getKey().equals("None")) {
						toContinue = true;
						otherDiags.addAll(entry.getValue());
					}

				}
			}

			if(toContinue == false) {
				continue;
			}

			Collection<SearchResult<String>> knn = lookupTable.nearestNeighboursVector(labelPair.getValue(), k);

			int amountInCluster = 0;

			for(SearchResult<String> toConvert: knn) {
				if(toConvert.payload == null) {
					continue;
				}
				double[] label = HelpFunctions.parse(toConvert.payload);

				Double toCheckIcd10 = new Double(label[3]);

				if(otherDiags.contains(toCheckIcd10)) {
					amountInCluster++;
				}

			}



			double clusterCovered = (double) amountInCluster / (double) k;

			if(icdCluster.containsKey(icd10)) {
				icdCluster.get(icd10).add(clusterCovered);
			}
			else {
				icdCluster.put(icd10, new ArrayList<Double>());
				icdCluster.get(icd10).add(clusterCovered);
			}

		}

		writer.writeLine("==RESULTS==");

		for (Map.Entry<Double, List<Double>> entry : icdCluster.entrySet())
		{

			writer.writeLine("" + HelpFunctions.icdDoubles.get(entry.getKey()) + " - " + entry.getValue().toString());

			double total = 0;
			for(double value: entry.getValue()) {
				total = total + value;
			}

			double average = total / (double) entry.getValue().size();

			writer.writeLine("" + HelpFunctions.icdDoubles.get(entry.getKey()) + " - Average:  " + average);

		}


	}

	public void checkClusters2(KNNLookupTable<StateImpl> lookupTable, Map<String, INDArray> newLabels, int k, ResultWriter writer) throws Exception {

		writer.writeLine("==CLUSTERTEST 2==");
		writer.writeLine("k: " + k);
		writer.writeLine("");

		Map<Double, Set<Double>> icdCluster = new HashMap<Double, Set<Double>>();
		Map<Double, Set<Double>> icdClusterRemoved = new HashMap<Double, Set<Double>>();

		for(Map.Entry<String, INDArray> labelPair : newLabels.entrySet()) {
			Double icd10 = HelpFunctions.parse(labelPair.getKey())[3];

			Set<Double> otherDiags = new HashSet<Double>();

			boolean toContinue = false;
			for (Map.Entry<String, Set<Double>> entry : clusters.entrySet())
			{
				if(entry.getValue().contains(icd10)) {
					if(!entry.getKey().equals("None")) {
						toContinue = true;
						otherDiags.addAll(entry.getValue());
					}

				}
			}

			if(toContinue == false) {
				continue;
			}

			if(icdCluster.containsKey(icd10)) {
				icdCluster.get(icd10).addAll(otherDiags);
			}
			else {
				icdCluster.put(icd10, new HashSet<Double>());
				icdCluster.get(icd10).addAll(otherDiags);
			}


			Collection<SearchResult<String>> knn = lookupTable.nearestNeighboursVector(labelPair.getValue(), k);


			for(SearchResult<String> toConvert: knn) {
				if(toConvert.payload == null) {
					continue;
				}
				double[] label = HelpFunctions.parse(toConvert.payload);

				Double toCheckIcd10 = new Double(label[3]);

				if(otherDiags.contains(toCheckIcd10)) {
					if(icdClusterRemoved.containsKey(icd10)) {
						icdClusterRemoved.get(icd10).add(toCheckIcd10);
					}
					else {
						icdClusterRemoved.put(icd10, new HashSet<Double>());
						icdClusterRemoved.get(icd10).add(toCheckIcd10);
					}
				}

			}


		}

		writer.writeLine("==RESULTS==");

		double total = 0.0;
		int n = 0;

		for (Map.Entry<Double, Set<Double>> entry : icdCluster.entrySet())
		{
			int originalSize = entry.getValue().size();

			if(originalSize == 0) {

				//writer.writeLine("" + entry.getKey() + " - " + "Nothing initial");

				continue;
			}

			if(icdClusterRemoved.containsKey(entry.getKey())) {
				int removedSize = icdClusterRemoved.get(entry.getKey()).size();

				double ratio = (double) removedSize / (double) originalSize;


				writer.writeLine("" + HelpFunctions.icdDoubles.get(entry.getKey()) + " - " + ratio);

				total = total + ratio;

			}
			else {
				writer.writeLine("" + HelpFunctions.icdDoubles.get(entry.getKey()) + " - " + 0.0);
			}

			n++;

		}

		writer.writeLine("");
		writer.writeLine("Average: " + total / (double) n);



	}

	private Double stringToDouble(String toConvert) {
		StringBuilder sb = new StringBuilder();
		for (char c : toConvert.toCharArray()) {
			sb.append((int) c);
		}

		Double toReturn = new Double(sb.toString());

		return toReturn;
	}



}
