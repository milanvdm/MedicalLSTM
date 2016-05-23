package plotting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Deepwalk2 {


	public int windowSize;
	public double learningRate;
	public int vectorLength;
	public int batchSize;
	public int epoch;
	public int minWordFreq;
	public int walkLength;
	public int k;

	public Map<String, Double> matches = new HashMap<String, Double>();

	public double totalAverage;

	public void parse(File file) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			boolean clusterTest = false;
			boolean results = false;

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
				if(line.contains("walklength")) {
					walkLength = Integer.parseInt(line.split(": ")[1]);
				}

				//PART 2
				if(line.contains("CLUSTERTEST 2")) {
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
					
					if(line.contains("Average: ")) {
						totalAverage = Double.parseDouble(line.split(": ")[1]);
						
						continue;
					}

					String[] splitted = line.split(" - ");

					String disease = splitted[0];
					Double average = Double.parseDouble(splitted[1]);

					matches.put(disease, average);

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
