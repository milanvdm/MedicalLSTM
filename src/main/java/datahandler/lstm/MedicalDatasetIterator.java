package datahandler.lstm;



import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.canova.api.io.WritableConverter;
import org.canova.api.io.converters.SelfWritableConverter;
import org.canova.api.io.converters.WritableConverterException;
import org.canova.api.records.reader.RecordReader;
import org.canova.api.records.reader.SequenceRecordReader;
import org.canova.api.writable.Writable;
import org.deeplearning4j.datasets.iterator.DataSetIterator;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.util.FeatureUtil;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class MedicalDatasetIterator<T extends SequenceElement> implements DataSetIterator {
	

	private static final long serialVersionUID = -806426663785696307L;
	
	protected RecordReader recordReader;
    protected WritableConverter converter;
    protected int batchSize = 1000;
    protected int labelIndex = -1;
    protected int numPossibleLabels = -1;
    protected boolean overshot = false;
    protected Iterator<Collection<Writable>> sequenceIter;
    protected DataSet last;
    protected boolean useCurrent = false;
    protected boolean regression = false;
    protected DataSetPreProcessor preProcessor;
    
    private final SequenceVectors<T> sequenceVectors;


    /**
     * Main constructor
     * @param recordReader the recorder to use for the dataset
     * @param batchSize the batch size
     * @param labelIndex the index of the label to use
     * @param numPossibleLabels the number of posible
     * @param sequenceVectors
     */
    public MedicalDatasetIterator(RecordReader recordReader, SequenceVectors<T> sequenceVectors, int batchSize, int labelIndex, int numPossibleLabels) {
        this(recordReader, sequenceVectors, new SelfWritableConverter(), batchSize, labelIndex, numPossibleLabels, false);
    }


    /**
     *
     * @param recordReader
     * @param converter
     * @param batchSize
     * @param labelIndex
     * @param numPossibleLabels
     * @param regression
     */
    public MedicalDatasetIterator(RecordReader recordReader, SequenceVectors<T> sequenceVectors, WritableConverter converter, int batchSize, int labelIndex, int numPossibleLabels, boolean regression) {
        this.recordReader = recordReader;
        this.converter = converter;
        this.batchSize = batchSize;
        this.labelIndex = labelIndex;
        this.numPossibleLabels = numPossibleLabels;
        this.regression = regression;
        this.sequenceVectors = sequenceVectors;
    }


    
    // COMBINE WITH:
    private DataSet nextDataSet(int num) throws IOException {
        //First: load reviews to String. Alternate positive and negative reviews
        List<String> reviews = new ArrayList<>(num);
        boolean[] positive = new boolean[num];
        for( int i=0; i<num && cursor<totalExamples(); i++ ){
            if(cursor % 2 == 0){
                //Load positive review
                int posReviewNumber = cursor / 2;
                String review = FileUtils.readFileToString(positiveFiles[posReviewNumber]);
                reviews.add(review);
                positive[i] = true;
            } else {
                //Load negative review
                int negReviewNumber = cursor / 2;
                String review = FileUtils.readFileToString(negativeFiles[negReviewNumber]);
                reviews.add(review);
                positive[i] = false;
            }
            cursor++;
        }

        //Second: tokenize reviews and filter out unknown words
        List<List<String>> allTokens = new ArrayList<>(reviews.size());
        int maxLength = 0;
        for(String s : reviews){
            List<String> tokens = tokenizerFactory.create(s).getTokens();
            List<String> tokensFiltered = new ArrayList<>();
            for(String t : tokens ){
                if(wordVectors.hasWord(t)) tokensFiltered.add(t);
            }
            allTokens.add(tokensFiltered);
            maxLength = Math.max(maxLength,tokensFiltered.size());
        }

        //If longest review exceeds 'truncateLength': only take the first 'truncateLength' words
        if(maxLength > truncateLength) maxLength = truncateLength;

        //Create data for training
        //Here: we have reviews.size() examples of varying lengths
        INDArray features = Nd4j.create(reviews.size(), vectorSize, maxLength);
        INDArray labels = Nd4j.create(reviews.size(), 2, maxLength);    //Two labels: positive or negative
        //Because we are dealing with reviews of different lengths and only one output at the final time step: use padding arrays
        //Mask arrays contain 1 if data is present at that time step for that example, or 0 if data is just padding
        INDArray featuresMask = Nd4j.zeros(reviews.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(reviews.size(), maxLength);

        int[] temp = new int[2];
        for( int i=0; i<reviews.size(); i++ ){
            List<String> tokens = allTokens.get(i);
            temp[0] = i;
            //Get word vectors for each word in review, and put them in the training data
            for( int j=0; j<tokens.size() && j<maxLength; j++ ){
                String token = tokens.get(j);
                INDArray vector = wordVectors.getWordVectorMatrix(token);
                features.put(new INDArrayIndex[]{NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);

                temp[1] = j;
                featuresMask.putScalar(temp, 1.0);  //Word is present (not padding) for this example + time step -> 1.0 in features mask
            }

            int idx = (positive[i] ? 0 : 1);
            int lastIdx = Math.min(tokens.size(),maxLength);
            labels.putScalar(new int[]{i,idx,lastIdx-1},1.0);   //Set label: [0,1] for negative, [1,0] for positive
            labelsMask.putScalar(new int[]{i,lastIdx-1},1.0);   //Specify that an output exists at the final time step for this example
        }

        return new DataSet(features,labels,featuresMask,labelsMask);
    }

    @Override
    public DataSet next(int num) {
        if(useCurrent) {
            useCurrent = false;
            if(preProcessor != null) preProcessor.preProcess(last);
            return last;
        }

        List<DataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if (!hasNext())
                break;
            if (recordReader instanceof SequenceRecordReader) {
                if(sequenceIter == null || !sequenceIter.hasNext()) {
                    Collection<Collection<Writable>> sequenceRecord = ((SequenceRecordReader) recordReader).sequenceRecord();
                    sequenceIter = sequenceRecord.iterator();
                }
                Collection<Writable> record = sequenceIter.next();
                dataSets.add(getDataSet(record));
            }

            else {
                Collection<Writable> record = recordReader.next();
                dataSets.add(getDataSet(record));
            }
        }
        List<INDArray> inputs = new ArrayList<>();
        List<INDArray> labels = new ArrayList<>();

        for (DataSet data : dataSets) {
            inputs.add(data.getFeatureMatrix());
            labels.add(data.getLabels());
        }

        if(inputs.isEmpty()) {
            overshot = true;
            return last;
        }

        DataSet ret =  new DataSet(Nd4j.vstack(inputs.toArray(new INDArray[0])), Nd4j.vstack(labels.toArray(new INDArray[0])));
        last = ret;
        if(preProcessor != null) preProcessor.preProcess(ret);
        return ret;
    }


    private DataSet getDataSet(Collection<Writable> record) {
        List<Writable> currList;
        if (record instanceof List)
            currList = (List<Writable>) record;
        else
            currList = new ArrayList<>(record);

        //allow people to specify label index as -1 and infer the last possible label
        if (numPossibleLabels >= 1 && labelIndex < 0) {
            labelIndex = record.size() - 1;
        }

        INDArray label = null;
        INDArray featureVector = Nd4j.create(labelIndex >= 0 ? currList.size()-1 : currList.size());
        int featureCount = 0;
        for (int j = 0; j < currList.size(); j++) {
            Writable current = currList.get(j);
            if (current.toString().isEmpty())
                continue;
            if (labelIndex >= 0 && j == labelIndex) {
                if (converter != null)
                    try {
                        current = converter.convert(current);
                    } catch (WritableConverterException e) {
                        e.printStackTrace();
                    }
                if (numPossibleLabels < 1)
                    throw new IllegalStateException("Number of possible labels invalid, must be >= 1");
                if (regression) {
                    label = Nd4j.scalar(current.toDouble());
                } else {
                    int curr = current.toInt();
                    if (curr >= numPossibleLabels)
                        curr--;
                    label = FeatureUtil.toOutcomeVector(curr, numPossibleLabels);
                }
            } else {
                featureVector.putScalar(featureCount++, current.toDouble());
            }
        }

        return new DataSet(featureVector, labelIndex >= 0 ? label : featureVector);
    }

    @Override
    public int totalExamples() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int inputColumns() {
        if(last == null) {
            DataSet next = next();
            last = next;
            useCurrent = true;
            return next.numInputs();
        }
        else
            return last.numInputs();

    }

    @Override
    public int totalOutcomes() {
        if(last == null) {
            DataSet next = next();
            last = next;
            useCurrent = true;
            return next.numOutcomes();
        }
        else
            return last.numOutcomes();


    }

    @Override
    public void reset() {
        if (recordReader instanceof RecordReader)
            recordReader.reset();
        else if (recordReader instanceof SequenceRecordReader)
            throw new UnsupportedOperationException("Reset not supported for SequenceRecordReader type.");
    }

    @Override
    public int batch() {
        return batchSize;
    }

    @Override
    public int cursor() {
        throw new UnsupportedOperationException();

    }

    @Override
    public int numExamples() {
        throw new UnsupportedOperationException();

    }

    @Override
    public void setPreProcessor(org.nd4j.linalg.dataset.api.DataSetPreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }



    @Override
    public boolean hasNext() {
        return recordReader.hasNext() || overshot;
    }

    @Override
    public DataSet next() {
        return next(batchSize);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getLabels(){
        return recordReader.getLabels();
    }
}


