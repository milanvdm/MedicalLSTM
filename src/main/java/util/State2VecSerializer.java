package util;


import data.StateImpl;
import state2vec.StateCache;

import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Loads word 2 vec models
 *
 * @author Adam Gibson
 */
public class State2VecSerializer {
	private static final Logger logger = LoggerFactory.getLogger(State2VecSerializer.class);


	/**
	 * @param modelFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static SequenceVectors<StateImpl> readTextModel(File modelFile) throws IOException, NumberFormatException {
		
		InMemoryLookupTable<StateImpl> lookupTable;
		VocabCache<StateImpl> cache;
		INDArray syn0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				GzipUtils.isCompressedFilename(modelFile.getName())
				? new GZIPInputStream(new FileInputStream(modelFile))
						: new FileInputStream(modelFile)))) {
			String line = reader.readLine();
			String[] initial = line.split(" ");
			int words = Integer.parseInt(initial[0].replace("\"", ""));
			int layerSize = Integer.parseInt(initial[1].replace("\"", ""));
			
			logger.debug("words: " + words + " | " + "layersize: " + layerSize);
			
			syn0 = Nd4j.create(words, layerSize);

			cache = new StateCache<StateImpl>();

			int currLine = 0;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(" ");
				assert split.length == layerSize + 1;
				String word = split[0].replace("~", " ").replace("\"", "");
				
				logger.debug("length: " + split.length);

				float[] vector = new float[split.length - 1];
				for (int i = 1; i < split.length; i++) {
					vector[i - 1] = Float.parseFloat(split[i].replace("\"", ""));
				}

				syn0.putRow(currLine, Transforms.unitVec(Nd4j.create(vector)));
				
				
				if (!cache.containsWord(word)) {
					StateImpl element = new StateImpl(word);
                    element.setElementFrequency(1);
    				cache.addWordToIndex(cache.numWords(), word);
                    cache.addToken(element);
                    
                } else {
                    cache.incrementWordCount(word);
                }

				currLine++;
			}
			
			logger.debug("syn0: " + syn0.toString());

			lookupTable = (InMemoryLookupTable<StateImpl>) new InMemoryLookupTable.Builder<StateImpl>()
					.cache(cache)
					.vectorLength(layerSize)
					.build();
			
			lookupTable.setSyn0(syn0);
			

			SequenceVectors<StateImpl> vectors = new SequenceVectors.Builder<StateImpl>()
					.vocabCache(cache)
					.lookupTable(lookupTable)
					.build();
			
			return vectors;
			
		}
		
	}

	/**
	 * This mehod writes word vectors to the given OutputStream.
	 * Please note: this method doesn't load whole vocab/lookupTable into memory, so it's able to process large vocabularies served over network.
	 *
	 * @param lookupTable
	 * @param stream
	 * @param <T>
	 * @throws IOException
	 */
	public static <T extends SequenceElement> void writeWordVectors(SequenceVectors<T> vectors, OutputStream stream) throws IOException {
		VocabCache<T> vocabCache = vectors.getVocab();
		WeightLookupTable<T> lookupTable = vectors.getLookupTable();

		PrintWriter writer = new PrintWriter(stream);
		
		writer.println(vocabCache.numWords() + " " + vectors.getLayerSize());

		for (int x = 0; x < vocabCache.numWords(); x++) {
			T element = vocabCache.elementAtIndex(x);

			StringBuilder builder = new StringBuilder();

			builder.append(element.getLabel().replaceAll(" ", "~")).append(" ");
			INDArray vec = lookupTable.vector(element.getLabel());
			for (int i = 0; i < vec.length(); i++) {
				builder.append(vec.getDouble(i));
				if (i < vec.length() - 1) builder.append(" ");
			}
			writer.println(builder.toString());
		}
		writer.flush();
		writer.close();
	}


	
	

	

}