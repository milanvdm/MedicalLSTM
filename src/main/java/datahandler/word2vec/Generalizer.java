package datahandler.word2vec;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;


public interface Generalizer {
	
	public Double getGeneralTimeDifference(double timeDifference);
	
	public Double getGeneralBirthYear(double birthYear);
	
	public Double decideSeason(Date date) throws ParseException;
	
	public Double decideICDCategory(double condition) throws IOException, InterruptedException;

}
