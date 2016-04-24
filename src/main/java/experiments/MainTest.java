package experiments;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Constants;

public class MainTest {

	protected static final Logger logger = LoggerFactory.getLogger(MainTest.class);

	public static void main(String[] args) throws Exception {

		logger.info("Started Main Test Version 1.0");

		File file = new File(Constants.INPUT_CSV_TEST);

		logger.info("Starting State2Vec Tests");

		//new State2VecTest(file);

		logger.info("Starting Knn Tests");

		new KnnTest(file);
	}


}
