package datahandler.lstm;



import org.canova.api.io.WritableConverter;
import org.canova.api.io.converters.SelfWritableConverter;
import org.canova.api.io.converters.WritableConverterException;
import org.canova.api.records.reader.RecordReader;
import org.canova.api.records.reader.SequenceRecordReader;
import org.canova.api.writable.Writable;
import org.deeplearning4j.datasets.iterator.DataSetIterator;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.FeatureUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.NotSupportedException;

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
		this(recordReader, sequenceVectors, new SelfWritableConverter(), batchSize, labelIndex, numPossibleLabels);
	}


	/**
	 *
	 * @param recordReader
	 * @param converter
	 * @param batchSize
	 * @param labelIndex
	 * @param numPossibleLabels
	 */
	public MedicalDatasetIterator(RecordReader recordReader, SequenceVectors<T> sequenceVectors, WritableConverter converter, int batchSize, int labelIndex, int numPossibleLabels) {
		this.recordReader = recordReader;
		this.converter = converter;
		this.batchSize = batchSize;
		this.labelIndex = labelIndex;
		this.numPossibleLabels = numPossibleLabels;
		this.sequenceVectors = sequenceVectors;
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
				throw new NotSupportedException("Only SequenceRecordReader is supported.");
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


	/**
	 * 
	 * IMPORTANT!
	 * This method has to be used to implement the following: 
	 * When a complete new example is found (not in the SequenceVectors) then we don't have to throw it away but approx
	 * it with a k-distance.
	 * 
	 * Also, this method makes it possible to use padding and masking -> Could give better results for irregular timeintervals.
	 * At the moment this is simply solved with the day difference attribute in the states. 
	 * 
	 */

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

		// Check if record is in the vocab
		List<Double> comparable = new ArrayList<Double>();
		for(Writable item: currList) {
			comparable.add(item.toDouble());
		}

		boolean inVocab = false;
		if(sequenceVectors.hasWord(comparable.toString())) {
			inVocab = true;
		}

		if(inVocab == false) {
			throw new NotSupportedException(); //TODO: k-instance
		}
		else {
			INDArray label = null;
			INDArray featureVector = Nd4j.create(labelIndex >= 0 ? currList.size()-1 : currList.size());
			int featureCount = 0;
			for (int j = 0; j < currList.size(); j++) {
				Writable current = currList.get(j);
				if (current.toString().isEmpty())
					continue;
				if (labelIndex >= 0 && j == labelIndex) {
					if (converter != null) {
						try {
							current = converter.convert(current);
						} catch (WritableConverterException e) {
							e.printStackTrace();
						}
					}
					if (numPossibleLabels < 1) {
						throw new IllegalStateException("Number of possible labels invalid, must be >= 1");
					}
					else {
						int curr = current.toInt();
						if (curr >= numPossibleLabels) {
							curr--;
						}
						label = FeatureUtil.toOutcomeVector(curr, numPossibleLabels);
					}
				} else {
					featureVector.putScalar(featureCount++, current.toDouble());
				}
			}
			
			return new DataSet(featureVector, labelIndex >= 0 ? label : featureVector);
			
			
		}
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
		recordReader.reset();
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


