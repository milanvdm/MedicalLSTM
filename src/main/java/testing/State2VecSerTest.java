package testing;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.word2vec.wordstore.VocabConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.StateImpl;
import datahandler.word2vec.MedicalSequenceIterator;
import state2vec.State2Vec;
import state2vec.StateCache;
import util.Constants;
import util.State2VecSerializer;

public class State2VecSerTest {
	
	protected static final Logger logger = LoggerFactory.getLogger(State2VecSerTest.class);
	
	public static void main(String[] args) throws Exception {
		
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
				.addSource(sequenceIterator, 3)
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
				.minWordFrequency(3)

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
				.iterations(1)

				// number of iterations over whole training corpus
				.epochs(1)

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
		
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(Constants.OUTPUT_WORD2VEC)));
		State2VecSerializer.writeWordVectors(vectors, stream);
		
		SequenceVectors<StateImpl> toTest = State2VecSerializer.readTextModel(new File(Constants.OUTPUT_WORD2VEC));
		
		logger.debug(vectors.getVocab().vocabWords().toString());
		logger.debug(toTest.getVocab().vocabWords().toString());
		
	}

}
