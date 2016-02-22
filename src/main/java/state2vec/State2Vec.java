package state2vec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.word2vec.wordstore.VocabConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import util.Constants;

public class State2Vec {

	//TODO: With next release: make distributed

	protected static final Logger logger = LoggerFactory.getLogger(State2Vec.class);

	public static void main(String[] args) throws Exception {

		logger.info("Started State2Vec");
		
		SequenceVectors<StateImpl> saved = loadState2VecModel();
		
		if(saved != null) {
			logger.info("Loaded existing model");
		}
		else {
			File file = new File(Constants.INPUT_CSV_TEST);

			/*
	        	Make a cache for the states
			 */
			StateCache<StateImpl> stateCache = new StateCache<StateImpl>();


			/*
	            Make a sequence iterator
			 */
			MedicalSequenceIterator<StateImpl> sequenceIterator = new MedicalSequenceIterator<>(file);

			/*
	        Now we should build vocabulary out of sequence iterator.
	        We can skip this phase, and just set AbstractVectors.resetModel(TRUE), and vocabulary will be mastered internally
			 */
			logger.info("Building Vocab");

			VocabConstructor<StateImpl> constructor = new VocabConstructor.Builder<StateImpl>()
					.addSource(sequenceIterator, 1)
					.setTargetVocabCache(stateCache)
					.build();

			constructor.buildJointVocabulary(false, true);

			/*
	            Time to build WeightLookupTable instance for our new model
			 */

			logger.info("Building LookupTable");
			WeightLookupTable<StateImpl> lookupTable = new InMemoryLookupTable.Builder<StateImpl>()
					.lr(0.025)
					.vectorLength(150) // Equals layersize if automatically managed -> duplicated code, removed in next version
					.useAdaGrad(true) // In short: prevents overfitting
					.cache(stateCache)
					.build();

			/*
	             reset model is viable only if you're setting AbstractVectors.resetModel() to false
	             if set to True - it will be called internally
			 */
			lookupTable.resetWeights(true);

			/*
	            Now we can build AbstractVectors model, that suits our needs
			 */
			logger.info("Building SequenceVectors");
			SequenceVectors<StateImpl> vectors = new SequenceVectors.Builder<StateImpl>(new VectorsConfiguration())
					// minimum number of occurencies for each element in training corpus. All elements below this value will be ignored
					// Please note: this value has effect only if resetModel() set to TRUE, for internal model building. Otherwise it'll be ignored, and actual vocabulary content will be used
					.minWordFrequency(1)

					// WeightLookupTable
					.lookupTable(lookupTable)

					// abstract iterator that covers training corpus
					.iterate(sequenceIterator)

					// vocabulary built prior to modelling
					.vocabCache(stateCache)

					// default value is 5 (for SkipGram)
					.windowSize(5)

					// batchSize is the number of sequences being processed by 1 thread at once
					// this value actually matters if you have iterations > 1
					.batchSize(250)

					// number of iterations over batch -> same as epochs if no shuffle is used!
					.iterations(3)

					// number of iterations over whole training corpus
					.epochs(3)

					// if set to true, vocabulary will be built from scratches internally
					// otherwise externally provided vocab will be used
					.resetModel(false)


					/*
	                    These two methods define our training goals. At least one goal should be set to TRUE.
					 */
					.trainElementsRepresentation(true)
					.trainSequencesRepresentation(false)


					.build();

			/*
	            Now, after all options are set, we just call fit()
			 */

			logger.info("Fitting State2Vec");
			vectors.fit();



			/*
	            As soon as fit() exits, model considered built, and we can test it.
	            Please note: all similarity context is handled via SequenceElement's labels, so if you're using AbstractVectors to build models for complex
	            objects/relations please take care of Labels uniqueness and meaning for yourself.
			 */

			//logger.info("Plotting State2Vec");
			//vectors.getLookupTable().plotVocab(); // BUGGED AT THE MOMENT IN DL4J
		}

		

	}

	private static void saveState2VecModel(SequenceVectors<StateImpl> vectors) {
		try{

			FileOutputStream fout = new FileOutputStream(Constants.OUTPUT_WORD2VEC);
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(vectors);
			oos.close();

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private static SequenceVectors<StateImpl> loadState2VecModel() {
		SequenceVectors<StateImpl> vectors;

		try{

			FileInputStream fin = new FileInputStream(Constants.OUTPUT_WORD2VEC);
			ObjectInputStream ois = new ObjectInputStream(fin);
			vectors = (SequenceVectors<StateImpl>) ois.readObject();
			ois.close();

			return vectors;

		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		} 
	}

}
