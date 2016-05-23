package plotting;

import java.io.File;

public class MakePlots {

	public static void main(String[] args) {

		String directory = " ";

		File dir = new File(directory);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				
				String name = child.getName();
				
				if(name.contains("Deepwalk")) {
					if(name.contains("Cluster1")) {
						
					}
					else {
						
					}
				}
				
				if(name.contains("Knn")) {
					if(name.contains("Cluster1")) {
						
					}
					else {
						
					}
				}
				
				if(name.contains("State2Vec")) {
					if(name.contains("Cluster1")) {
						
					}
					else {
						
					}
				}
				
			}

		}

	}
}
