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

import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import util.CsvIterator;
import util.HelpFunctions;

public class ClusterSeqTest {

	protected static final Logger logger = LoggerFactory.getLogger(ClusterSeqTest.class);

	public Map<String, Set<Double>> clusters = new HashMap<String, Set<Double>>();
	public Map<Double, Integer> allDiseasesInClusters = new HashMap<Double, Integer>();

	public ClusterSeqTest() throws IOException, InterruptedException {
		readClusters();
	}

	private void readClusters() throws IOException, InterruptedException {
		File file = new File("clusters.csv");
		//File file = new File("clusters/clusters.csv");

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
			
			allDiseasesInClusters.put(stringToDouble(diag1), 0);
			allDiseasesInClusters.put(stringToDouble(diag2), 0);
			allDiseasesInClusters.put(stringToDouble(diag3), 0);
			allDiseasesInClusters.put(stringToDouble(diag4), 0);
		}

		//logger.info(clusters.toString());
		//String info = "" + clusters.size();
		//logger.info(info);


	}

	public void checkClusters1(SequenceVectors<StateImpl> sequenceVectors, int k, ResultWriter writer) throws Exception {

		writer.writeLine("==CLUSTERTEST 1==");
		writer.writeLine("k: " + k);
		writer.writeLine("");

		WeightLookupTable<StateImpl> table = sequenceVectors.getLookupTable();

		//String info = "" + table.getVocabCache().numWords();
		//logger.info(info);

		Map<Double, List<Double>> icdCluster = new HashMap<Double, List<Double>>();
		Map<Double, List<Integer>> totalAmounts = new HashMap<Double, List<Integer>>();

		for(StateImpl state: table.getVocabCache().vocabWords()) {
			Double icd10 = state.getState2vecLabel().get(3);

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
			//otherDiags.remove(icd10);

			if(toContinue == false) {
				continue;
			}

			int initAmount = otherDiags.size();

			Collection<String> knn = sequenceVectors.wordsNearest(state.getLabel(), k);


			//logger.info(knn.toString());

			int amountInCluster = 0;

			for(String toConvert: knn) {
				double[] label = HelpFunctions.parse(toConvert);

				Double toCheckIcd10 = new Double(label[3]);

				if(otherDiags.contains(toCheckIcd10)) {
					amountInCluster++;
				}

			}
			
			int totalAmountInCluster = 0;
			
			for(String toConvert: knn) {
				double[] label = HelpFunctions.parse(toConvert);

				Double toCheckIcd10 = new Double(label[3]);

				if(allDiseasesInClusters.containsKey(toCheckIcd10)) {
					totalAmountInCluster++;
				}

			}


			double clusterCovered = (double) amountInCluster / (double) totalAmountInCluster;

			if(icdCluster.containsKey(icd10)) {
				icdCluster.get(icd10).add(clusterCovered);
				totalAmounts.get(icd10).add(totalAmountInCluster);
			}
			else {
				icdCluster.put(icd10, new ArrayList<Double>());
				icdCluster.get(icd10).add(clusterCovered);
				
				totalAmounts.put(icd10, new ArrayList<Integer>());
				totalAmounts.get(icd10).add(totalAmountInCluster);
			}

		}

		writer.writeLine("==RESULTS==");

		for (Map.Entry<Double, List<Double>> entry : icdCluster.entrySet())
		{

			writer.writeLine("TotalsInClusters: " + totalAmounts.get(entry.getKey()).toString());
			writer.writeLine("" + HelpFunctions.icdDoubles.get(entry.getKey()) + " - " + entry.getValue().toString());

			double total = 0;
			for(double value: entry.getValue()) {
				total = total + value;
			}

			double average = total / (double) entry.getValue().size();

			writer.writeLine("" + HelpFunctions.icdDoubles.get(entry.getKey()) + " - Average:  " + average);

		}


	}

	public void checkClusters2(SequenceVectors<StateImpl> sequenceVectors, int k, ResultWriter writer) throws Exception {

		writer.writeLine("==CLUSTERTEST 2==");
		writer.writeLine("k: " + k);
		writer.writeLine("");

		WeightLookupTable<StateImpl> table = sequenceVectors.getLookupTable();

		Map<Double, Set<Double>> icdCluster = new HashMap<Double, Set<Double>>();
		Map<Double, Set<Double>> icdClusterRemoved = new HashMap<Double, Set<Double>>();

		for(StateImpl state: table.getVocabCache().vocabWords()) {
			Double icd10 = state.getState2vecLabel().get(3);

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


			Collection<String> knn = sequenceVectors.wordsNearest(state.getLabel(), k);


			for(String toConvert: knn) {
				double[] label = HelpFunctions.parse(toConvert);

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
