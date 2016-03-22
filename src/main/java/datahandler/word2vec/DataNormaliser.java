package datahandler.word2vec;

import java.io.IOException;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;

import data.StateImpl;
import util.Constants;

public class DataNormaliser {

	private MedicalSequenceIterator<StateImpl> currentIterator;

	private double[] means;
	private double[] stds;


	public DataNormaliser(MedicalSequenceIterator<StateImpl> iterator) throws IOException, InterruptedException {
		this.currentIterator = iterator;

		means = new double[2];
		stds = new double[2];

		normalise();
	}

	// Naive implementation of calculating means and variances
	private void normalise() throws IOException, InterruptedException {
		int n = 0;

		while(currentIterator.hasMoreSequences()) {
			Sequence<StateImpl> sequence = currentIterator.nextSequence();


			for(StateImpl state: sequence.getElements()) {
				means[0] = means[0] + state.getState2vecLabel().get(Constants.BIRTH_YEAR_COLUMN);
				means[1] = means[1] + state.getState2vecLabel().get(Constants.TIME_DIFFERENCE_COLUMN);
			}



			n++;
		}

		int i = 0;
		while(i < means.length) {
			means[i] = means[i] / n;
			i++;
		}

		currentIterator.reset(); //reset
		
		while(currentIterator.hasMoreSequences()) {
			Sequence<StateImpl> sequence = currentIterator.nextSequence();


			for(StateImpl state: sequence.getElements()) {
				stds[0] = stds[0] + (state.getState2vecLabel().get(Constants.BIRTH_YEAR_COLUMN) - means[0]) * (state.getState2vecLabel().get(Constants.BIRTH_YEAR_COLUMN) - means[0]);
				stds[0] = stds[0] + (state.getState2vecLabel().get(Constants.TIME_DIFFERENCE_COLUMN) - means[1]) * (state.getState2vecLabel().get(Constants.TIME_DIFFERENCE_COLUMN) - means[1]);
			}

		}

		i = 0;
		while(i < stds.length) {
			stds[i] = stds[i] / (n-1);
			i++;
		}

	}

	public double[] getMeans() {
		return means;
	}

	public double[] getStds() {
		return stds;
	}

}
