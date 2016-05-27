package experiments;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Constants;

public class MainTest {

	protected static final Logger logger = LoggerFactory.getLogger(MainTest.class);

	public static void main(String[] args) throws Exception {

		logger.info("Started Main Test Version 2.1");

		
		//File file1 = new File(Constants.INPUT_CSV_TEST);
		//logger.info("Starting State2Vec Tests");

		//new State2VecTest(file1, "0");
		
		
		if(args[0].equals("state2vec")) {
			File file = new File("conditions_sorted.csv");
			
			logger.info("Starting State2Vec Tests");

			//new State2VecTest(file, "0");
			new State2VecTest(file, args[1]);
		}
		if(args[0].equals("knn")) {
			File trainingFile = new File("training.csv");
			File testFile = new File("test.csv");
			
			logger.info("Starting Knn Tests");

			new KnnTest(trainingFile, testFile, args[1]);
		}
		if(args[0].equals("deepwalk")) {
			File deepwalkFile = new File("deepwalk.csv");
			
			logger.info("Starting Deepwalk Tests");

			new DeepWalkTest(deepwalkFile, args[1]);
		}

		

		
		
		
	}


}
