package experiments;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Constants;

public class MainTest {

	protected static final Logger logger = LoggerFactory.getLogger(MainTest.class);

	public static void main(String[] args) throws Exception {

		logger.info("Started Main Test Version 1.7");

		//File file = new File("conditions_sorted.csv");
		File file = new File(Constants.INPUT_CSV_TEST);
		//File trainingFile = new File("training.csv");
		//File testFile = new File("test.csv");

		//logger.info("Starting State2Vec Tests");

		//new State2VecTest(file, "0");
		//new State2VecTest(file, args[0]);

		//logger.info("Starting Knn Tests");

		//new KnnTest(trainingFile, testFile, args[0]);
		
		logger.info("Starting Deepwalk Tests");

		new DeepWalkTest(file);
	}


}
