package datahandler.word2vec;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.nd4j.linalg.api.ndarray.INDArray;

import data.StateImpl;

public class AdvancedGeneralizer implements Generalizer {
	
	private SimpleGeneralizer simple = new SimpleGeneralizer();
	
	private DataNormaliser normaliser;
	
	public AdvancedGeneralizer(DataNormaliser normaliser) {
		this.normaliser = normaliser;
	}

	
	public Double getGeneralTimeDifference(double timeDifference) {
		INDArray normalized = toNormalize.subi(mean).divi(std);
	}

	
	public Double getGeneralBirthYear(double birthYear) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Double decideSeason(Date date) throws ParseException {
		return simple.decideSeason(date);
	}

	
	public Double decideICDCategory(double condition) throws IOException, InterruptedException {
		return simple.decideICDCategory(condition);
	}


	

}
