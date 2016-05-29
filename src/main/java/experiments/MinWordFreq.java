package experiments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MinWordFreq {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader("/media/milan/Data/Thesis/Results/vocab_general.txt"))) {

			int totalInstances = 0;
			int instancesAbove10 = 0;

			String line;
			while ((line = br.readLine()) != null) {
				if(line.equals("")) {
					continue;
				}

				if(line.contains("GENERAL")) {
					continue;
				}

				String[] splitted = line.split(" - ");

				int freq = Integer.parseInt(splitted[1]);

				if(freq > 10) {
					instancesAbove10 = instancesAbove10 + freq;
				}

				totalInstances = totalInstances + freq;
			}
			
			System.out.println((double) instancesAbove10 / (double) totalInstances);
		}

	}
}