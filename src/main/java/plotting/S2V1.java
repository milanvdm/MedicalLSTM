package plotting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S2V1 {

	public int windowSize;
	public double learningRate;
	public int vectorLength;
	public int batchSize;
	public int epoch;
	public int minWordFreq;
	
	
	public int k;
	
	public Map<String, List<Integer>> totalsInClusters = new HashMap<String, List<Integer>>();
	public Map<String, List<Double>> matchesClusters = new HashMap<String, List<Double>>();
	public Map<String, Double> averageClusters = new HashMap<String, Double>();

	public void parse(File file) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    
			boolean clusterTest = false;
			boolean results = false;
			int resultCounter = 0;
			Integer[] intParsed = null;
			
			String line;
		    while ((line = br.readLine()) != null) {
		       if(line.equals("")) {
		    	   continue;
		       }
		       
		       //PART 1
		       if(line.contains("windowSize")) {
		    	   windowSize = Integer.parseInt(line.split(": ")[1]);
		       }
		       if(line.contains("learningRate")) {
		    	   learningRate = Double.parseDouble(line.split(": ")[1]);
		       }
		       if(line.contains("vectorLength")) {
		    	   vectorLength = Integer.parseInt(line.split(": ")[1]);
		       }
		       if(line.contains("batchSize")) {
		    	   batchSize = Integer.parseInt(line.split(": ")[1]);
		       }
		       if(line.contains("epoch")) {
		    	   epoch = Integer.parseInt(line.split(": ")[1]);
		       }
		       if(line.contains("minWordFreq")) {
		    	   minWordFreq = Integer.parseInt(line.split(": ")[1]);
		       }
		       
		       //PART 2
		       if(line.contains("CLUSTERTEST 1")) {
		    	   clusterTest = true;
		       }
		       if(clusterTest) {
		    	   k = Integer.parseInt(line.split(": ")[1]);
		    	   clusterTest = false;
		       }
		       
		       //PART 3
		       if(line.contains("RESULTS")) {
		    	   results = true;
		       }
		       if(results) {
		    	   if(resultCounter == 0) {
		    		   intParsed = parseIntList(line.split(": ")[1]);
		    		   resultCounter++;
		    	   }
		    	   if(resultCounter == 1) {
		    		   String[] splitted = line.split(" - ");
		    		   String disease = splitted[0];
		    		   Double [] parsed = parseDoubleList(splitted[1]);
		    		   
		    		   totalsInClusters.put(disease, Arrays.asList(intParsed));
		    		   matchesClusters.put(disease, Arrays.asList(parsed));
		    		   resultCounter++;
		    	   }
		    	   if(resultCounter == 3) {
		    		   String[] splitted = line.split(" - Average:  ");
		    		   
		    		   String disease = splitted[0];
		    		   Double average = Double.parseDouble(splitted[1]);
		    		   
		    		   averageClusters.put(disease, average);
		    		   resultCounter = 0;
		    	   }
		       }
		    }
		}
	}
	
	public static Double[] parseDoubleList(String s) {
		String[] strings = s.replace("[", "").replace("]", "").split(", ");
		Double result[] = new Double[strings.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Double.parseDouble(strings[i]);
		}
		return result;
	}
	
	public static Integer[] parseIntList(String s) {
		String[] strings = s.replace("[", "").replace("]", "").split(", ");
		Integer result[] = new Integer[strings.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(strings[i]);
		}
		return result;
	}
	
}
