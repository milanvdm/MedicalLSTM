package datahandler.word2vec;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class AdvancedGeneralized implements Generalizer {
	
	SimpleGeneralizer simple = new SimpleGeneralizer();

	@Override
	public Double getGeneralTimeDifference(double timeDifference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getGeneralBirthYear(double birthYear) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double decideSeason(Date date) throws ParseException {
		return simple.decideSeason(date);
	}

	@Override
	public Double decideICDCategory(double condition) throws IOException, InterruptedException {
		return simple.decideICDCategory(condition);
	}

}
